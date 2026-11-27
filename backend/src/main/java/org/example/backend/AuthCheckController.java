package org.example.backend;

import java.util.Map;

import org.example.backend.users.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Victoria Ha
 * @version 0.1.1
 * Description: authController uwu now manages sessioning and checks
 *              whether idenetities have db admin access or not
 *              changed upon implementing the 409 rejection
 * @since 11/19/2026
 */
@RestController
public class AuthCheckController {

    private final UserService userService;

    public AuthCheckController(UserService userService) {
        this.userService = userService;
    }

    //adds stored email
    //TODO: VERIFY /API/ME RETURNS THE STORED EMAIL ONCE sUPABASE IS RESUMED
    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        var user = userService.requireUser(authentication);

        return Map.of(
                "authenticated", true,
                "provider", user.getProvider(),
                "providerUserId", user.getAuthSub(),
                "isAdmin", user.isAdmin(),
                "email", user.getEmail()
        );
    }
}