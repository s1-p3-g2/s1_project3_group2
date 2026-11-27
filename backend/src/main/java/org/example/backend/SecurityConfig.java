package org.example.backend;

import java.util.List;
import java.util.Map;
import org.example.backend.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ResponseStatusException;

/**
 * Configures route permissions, OAuth login, and frontend cross-origin access.
 *
 * @author Paul Conner & Victoria Ha
 * @version 0.1.1
 * @since 11/27/2026
 */
@Configuration
public class SecurityConfig {

  private static final String RATINGS_PATH = "/api/v1/ratings";
  private static final String RATINGS_SUBPATHS = "/api/v1/ratings/**";
  private static final int MAX_USERNAME_LENGTH = 100;
  private static final String GOOGLE_PROVIDER = "google";
  private static final String GITHUB_PROVIDER = "github";

  // temporary logging stuff?
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

  /**
   * Builds the security rules and OAuth login success handling.
   *
   * @param http the HTTP security configuration
   * @param userService the service used to find or create app users
   * @param authorizedClientService the service managing provider authorizations
   * @return the configured security filter chain
   * @throws Exception if the security configuration cannot be built
   */
  // Determines which routes to protect with oauth
  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      UserService userService,
      OAuth2AuthorizedClientService authorizedClientService)
      throws Exception {
    // Return to the requested page, or /api/me if none was saved.
    var redirectHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    redirectHandler.setDefaultTargetUrl("/api/me");

    http
        .csrf(csrf -> csrf.disable()) // TODO: temporary, bypasses oauth to test ratings
        .cors(cors -> {})
        .authorizeHttpRequests(
            auth ->
                auth
                    .requestMatchers("/api/v1/ratings/me/**").authenticated()
                    .requestMatchers("/api/me").authenticated()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET, RATINGS_PATH, RATINGS_SUBPATHS)
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, RATINGS_PATH, RATINGS_SUBPATHS)
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, RATINGS_PATH, RATINGS_SUBPATHS)
                    .permitAll()
                    .requestMatchers(HttpMethod.DELETE, RATINGS_PATH, RATINGS_SUBPATHS)
                    .permitAll()
                    .anyRequest().authenticated())
        .oauth2Login(
            oauth ->
                oauth.successHandler(
                    (request, response, authentication) -> {
                      // this bit basically the checkpoint between git/goog auth'd you
                      // and the app accepts you
                      // doesnt make the jwt or log them out just decides whether their
                      // successful provider login is accepted by the app.
                      // ID the acc, read profile info, find or create the db user
                      // (this is where the 409 conflict fits in)
                      // (or existing account, return it)
                      // (or new unconflicted acc, create it)
                      // finish logging in or finish rejecting
                      // (like clearing the auth session and trurning the codes)

                      var token = (OAuth2AuthenticationToken) authentication;

                      String provider = token.getAuthorizedClientRegistrationId();
                      String providerUserId = token.getName();

                      try {
                        String email = getVerifiedEmail(token, authorizedClientService);

                        // Get initial profile details from the authenticated provider.
                        String username =
                            token.getPrincipal()
                                .getAttribute(
                                    GITHUB_PROVIDER.equals(provider) ? "login" : "name");
                        String profilePic =
                            token.getPrincipal()
                                .getAttribute(
                                    GITHUB_PROVIDER.equals(provider) ? "avatar_url" : "picture");

                        // Ensure the initial username fits the model's requirements.
                        if (username == null || username.isBlank()) {
                          username = provider + " user";
                        }
                        if (username.length() > MAX_USERNAME_LENGTH) {
                          username = username.substring(0, MAX_USERNAME_LENGTH);
                        }

                        // Create first-time users; preserve existing users.
                        userService.findOrCreateUser(
                            provider, providerUserId, username, profilePic, email);
                      } catch (RuntimeException exception) {
                        // Provider login succeeded, but app sign-in was rejected.
                        // Clear the authenticated session before returning an error.
                        LOGGER.error(
                            "App sign-in failed for provider {}", provider, exception);
                        new SecurityContextLogoutHandler()
                            .logout(request, response, authentication);

                        authorizedClientService.removeAuthorizedClient(
                            provider, providerUserId);

                        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
                        String message = "Unable to complete sign-in.";

                        if (exception instanceof ResponseStatusException rejection) {
                          status = rejection.getStatusCode().value();

                          message =
                              switch (status) {
                                case 409 ->
                                    "This email is registered"
                                        + " with another sign-in provider.";
                                case 403 ->
                                    "A verified email from a supported"
                                        + " provider is required.";
                                case 401 ->
                                    "Provider authorization is unavailable."
                                        + " Please sign in again.";
                                case 502 ->
                                    "Unable to verify your email"
                                        + " with the provider. Try again.";
                                default -> "Unable to complete sign-in.";
                              };
                        }

                        response.setStatus(status);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"message\":\"" + message + "\"}");
                        return;
                      }

                      redirectHandler.onAuthenticationSuccess(
                          request, response, authentication);
                    }));

    return http.build();
  }

  /**
   * Gets a verified email from the authenticated provider.
   * GitHub accounts must have a verified primary email.
   *
   * @param token the authenticated OAuth identity
   * @param authorizedClientService the service providing the provider access token
   * @return the verified email with surrounding whitespace removed
   * @throws ResponseStatusException if verification fails or the provider is unsupported
   */
  private String getVerifiedEmail(
      OAuth2AuthenticationToken token,
      OAuth2AuthorizedClientService authorizedClientService) {
    String provider = token.getAuthorizedClientRegistrationId();

    if (GOOGLE_PROVIDER.equals(provider)) {
      Boolean verified = token.getPrincipal().getAttribute("email_verified");
      String email = token.getPrincipal().getAttribute("email");

      if (!Boolean.TRUE.equals(verified) || email == null || email.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, "A verified Google email is required");
      }

      return email.strip();
    }

    if (GITHUB_PROVIDER.equals(provider)) {
      OAuth2AuthorizedClient client =
          authorizedClientService.loadAuthorizedClient(provider, token.getName());

      if (client == null) {
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "GitHub authorization is unavailable");
      }

      List<Map<String, Object>> emails;

      try {
        emails =
            RestClient.create()
                .get()
                .uri("https://api.github.com/user/emails?per_page=100")
                .headers(
                    headers -> {
                      headers.setBearerAuth(client.getAccessToken().getTokenValue());
                      headers.set("Accept", "application/vnd.github+json");
                      headers.set("User-Agent", "Rate-My-Duck");
                    })
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
      } catch (RestClientException exception) {
        throw new ResponseStatusException(
            HttpStatus.BAD_GATEWAY, "Unable to verify GitHub email", exception);
      }

      if (emails != null) {
        for (Map<String, Object> entry : emails) {
          if (Boolean.TRUE.equals(entry.get("primary"))
              && Boolean.TRUE.equals(entry.get("verified"))
              && entry.get("email") instanceof String email
              && !email.isBlank()) {
            return email.strip();
          }
        }
      }

      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "A verified primary GitHub email is required");
    }

    throw new ResponseStatusException(
        HttpStatus.FORBIDDEN, "Unsupported sign-in provider");
  }

  /**
   * Allows credentialed cross-origin requests from the local Expo frontend.
   *
   * @return the CORS configuration applied to all routes
   */
  // Need cors config for expo apparently
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // better config has been added
    config.setAllowedOrigins(List.of("http://localhost:8081"));
    config.setAllowedMethods(List.of("*"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);
    return source;
  }
}