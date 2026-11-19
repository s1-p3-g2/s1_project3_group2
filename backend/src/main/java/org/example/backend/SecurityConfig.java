package org.example.backend;
/**
 * @author Paul Conner & Victoria Ha
 * @version 0.1.0
 * Description:
 * @since 11/16/2026
 */

import java.util.List;

import org.example.backend.users.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    // Determines which routes to protect with oauth
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserService userService
    ) throws Exception {
        // Return to the requested page, or /api/me if none was saved.
        var redirectHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        redirectHandler.setDefaultTargetUrl("/api/me");

        http
                .csrf(csrf -> csrf.disable()) // TODO: temporary, bypasses oauth to test ratings
                .cors(cors -> {
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/ratings/me/**").authenticated()
                        .requestMatchers("/api/me").authenticated()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/ratings",
                                "/api/v1/ratings/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/ratings",
                                "/api/v1/ratings/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/ratings",
                                "/api/v1/ratings/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/ratings",
                                "/api/v1/ratings/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {
                            var token = (OAuth2AuthenticationToken) authentication;

                            String provider = token.getAuthorizedClientRegistrationId();
                            String providerUserId = token.getName();

                            // Get initial profile details from the authenticated provider.
                            String username = token.getPrincipal().getAttribute(
                                    "github".equals(provider) ? "login" : "name"
                            );
                            String profilePic = token.getPrincipal().getAttribute(
                                    "github".equals(provider) ? "avatar_url" : "picture"
                            );

                            // Ensure the initial username fits the model's requirements.
                            if (username == null || username.isBlank()) {
                                username = provider + " user";
                            }
                            if (username.length() > 100) {
                                username = username.substring(0, 100);
                            }

                            // Create first-time users; preserve existing users.
                            userService.findOrCreateUser(
                                    provider,
                                    providerUserId,
                                    username,
                                    profilePic,
                                    email
                            );

                            redirectHandler.onAuthenticationSuccess(
                                    request,
                                    response,
                                    authentication
                            );
                        })
                );

        return http.build();
    }

    // Need cors config for expo apparently
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Just allow everything, maybe do better config later
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}