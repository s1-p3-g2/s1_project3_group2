package org.example.backend;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  // Determines which routes to protect with oauth
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> {})
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/v1/ratings/**")
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/ratings/**").permitAll() // TODO: temporary, bypasses oauth to test ratings
            .requestMatchers(HttpMethod.POST, "/api/v1/ratings/**").permitAll() // TODO: temporary, bypasses oauth to test ratings
            .requestMatchers(HttpMethod.PUT, "/api/v1/ratings/**").permitAll() // TODO: temporary, bypasses oauth to test ratings
            .requestMatchers("/api/me").authenticated()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> {});

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