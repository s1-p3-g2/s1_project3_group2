package org.example.backend;

import java.util.Map;
import org.example.backend.users.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Returns the current user's authentication status and stored account information.
 *
 * <p>Description: authController uwu now manages sessioning and checks
 * whether idenetities have db admin access or not
 * changed upon implementing the 409 rejection
 *
 * @author Victoria Ha
 * @version 0.1.2
 * @since 11/27/2026
 */
@RestController
public class AuthCheckController {

  private final UserService userService;

  /**
   * Creates the controller with the user lookup service.
   *
   * @param userService the service used to retrieve the authenticated user
   */
  public AuthCheckController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Returns the authenticated user's provider identity, admin status, and email.
   *
   * @param authentication the current request's authentication
   * @return the authentication status and stored user information
   */
  // adds stored email
  // TODO: Verify /api/me returns the stored email once Supabase is resumed.
  @GetMapping("/api/me")
  public Map<String, Object> me(Authentication authentication) {
    var user = userService.requireUser(authentication);

    return Map.of(
        "authenticated", true,
        "provider", user.getProvider(),
        "providerUserId", user.getAuthSub(),
        "isAdmin", user.isAdmin(),
        "email", user.getEmail());
  }
}