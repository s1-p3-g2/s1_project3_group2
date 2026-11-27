package org.example.backend.users;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles requests to change a user's administrator status.
 *
 * <p>Description: USer Controller with Patch/api/users/user_id/admin
 *
 * @author Victoria Ha
 * @version 0.1.2
 * @since 11/27/2026
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  /**
   * Creates the controller with the user management service.
   *
   * @param userService the service that enforces permissions and updates users
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Contains the requested administrator status.
   *
   * @param isAdmin whether the target user should have administrator access
   */
  public record AdminStatusRequest(Boolean isAdmin) {
  }

  /**
   * Changes the target user's administrator status after checking permissions.
   *
   * @param authentication the current request's authentication
   * @param userId         the database ID of the target user
   * @param request        the requested administrator status
   */
  @PatchMapping("/{userId}/admin")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changeAdminStatus(
      Authentication authentication,
      @PathVariable("userId") Long userId,
      @RequestBody AdminStatusRequest request) {
    if (request.isAdmin() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isAdmin is required");
    }

    userService.changeAdminStatus(authentication, userId, request.isAdmin());
  }
}