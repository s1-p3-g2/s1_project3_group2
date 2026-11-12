package org.example.backend;

import java.util.Map;

import org.example.backend.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
/**
 * @author Victoria Ha
 * @version 0.1.1
 * Description: authController uwu now manages sessioning and checks
 *              whether idenetities have db admin access or not
 * @since 11/9/2026
 */
@RestController
public class AuthCheckController {

    private final UserService userService;

    public AuthCheckController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth)
                || !oauth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String provider = oauth.getAuthorizedClientRegistrationId();
        String providerUserId = oauth.getName();

        return Map.of(
                "authenticated", true,
                "provider", provider,
                "providerUserId", providerUserId,
                "isAdmin", userService.isAdmin(provider, providerUserId)
        );
    }
}