package org.example.backend.users;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Manages user lookup, OAuth provisioning, and administrator permissions.
 *
 * <p>Description: God(me, the author) hates me(the users) and everything I stand for(api
 * routes involving them)
 * also this decides what happens with the data from the User Repo
 * i think the most important methods here are isAdmin() and findUser() and requireAdmin
 *
 * @author Victoria Ha
 * @version 0.1.0
 * @since 11/9/2026
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  /**
   * Creates the service with the user repository.
   *
   * @param userRepository the repository used to access stored users
   */
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Finds a user by their OAuth provider identity.
   *
   * @param provider       the OAuth provider
   * @param providerUserId the provider-specific user ID
   * @return the matching database user
   * @throws ResponseStatusException if no matching user exists
   */
  // User lookup
  public User findUser(String provider, String providerUserId) {
    return userRepository
        .findByProviderAndAuthSub(provider, providerUserId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  /**
   * Returns the existing app user, or creates one on their first login.
   * Identity and profile values must come from the authenticated OAuth session.
   * Existing profiles and administrator permissions are preserved.
   * Rejects emails already associated with a different OAuth provider.
   *
   * @param provider       the OAuth provider
   * @param providerUserId the provider-specific user ID
   * @param username       the initial display name
   * @param profilePic     the initial profile picture link
   * @param email          the verified email supplied by the provider
   * @return the existing or newly saved user
   * @throws ResponseStatusException if the email is missing or belongs to another provider
   */
  @Transactional
  public User findOrCreateUser(
      String provider,
      String providerUserId,
      String username,
      String profilePic,
      String email) {
    if (email == null || email.isBlank()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "An email address is required to sign in");
    }

    String trimmedEmail = email.strip();

    if (userRepository.existsByEmailIgnoreCaseAndProviderNot(trimmedEmail, provider)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "This email is already registered with a different sign-in provider");
    }

    return userRepository
        .findByProviderAndAuthSub(provider, providerUserId)
        .orElseGet(
            () -> {
              User newUser =
                  new User(username, provider, providerUserId, null, profilePic, trimmedEmail);

              return userRepository.save(newUser);
            });
  }

  /**
   * Checks the stored administrator status for a provider identity.
   *
   * @param provider       the OAuth provider
   * @param providerUserId the provider-specific user ID
   * @return true if the user exists and has administrator access
   */
  // admin check
  public boolean isAdmin(String provider, String providerUserId) {
    return userRepository
        .findByProviderAndAuthSub(provider, providerUserId)
        .map(User::isAdmin)
        .orElse(false);
  }

  /**
   * Requires the current user to have administrator access.
   *
   * @param authentication the current request's authentication
   * @throws ResponseStatusException if the caller lacks an app account or administrator access,
   *                                 or is not authenticated
   */
  // RequireAd- yeah honestly.
  private void requireAdmin(Authentication authentication) {
    User currentUser = requireUser(authentication);

    if (!currentUser.isAdmin()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Administrator access is required");
    }
  }

  /**
   * Changes a target user's administrator status after checking the caller's permissions.
   *
   * @param authentication the current request's authentication
   * @param targetUserId   the database ID of the user to update
   * @param admin          whether the target should have administrator access
   * @throws ResponseStatusException if the caller is not authorized or the target does not exist
   */
  // majorly important maybe don't touch without telling someone.
  // this method makes or breaks about 70% of the functionality of the coming issues.
  @Transactional
  public void changeAdminStatus(
      Authentication authentication, Long targetUserId, boolean admin) {
    requireAdmin(authentication);

    User targetUser =
        userRepository
            .findById(targetUserId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    targetUser.setAdmin(admin);
    userRepository.save(targetUser);
  }

  /**
   * Lists stored user profiles for an administrator.
   *
   * @param authentication the current request's authentication
   * @return the stored user profiles
   * @throws ResponseStatusException if the caller is not authorized
   */
  @Transactional(readOnly = true)
  public List<UserResponse> getUsers(Authentication authentication) {
    requireAdmin(authentication);

    return userRepository.findAll().stream()
        .map(
            user ->
                new UserResponse(
                    user.getUserId(),
                    user.getUsername(),
                    user.getBio(),
                    user.getProfilePic(),
                    user.isAdmin(),
                    user.getEmail()))
        .toList();
  }

  /**
   * Looks up the database user associated with the authenticated OAuth identity.
   *
   * @param authentication the current request's authentication
   * @return the matching database user
   * @throws ResponseStatusException if OAuth authentication or the database user is missing
   */
  // unpacks user from the autho session. paul might use this later idrk..
  @Transactional(readOnly = true)
  public User requireUser(Authentication authentication) {
    if (!(authentication instanceof OAuth2AuthenticationToken oauth)
        || !oauth.isAuthenticated()) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "OAuth sign-in is required");
    }

    String provider = oauth.getAuthorizedClientRegistrationId();
    String providerUserId = oauth.getName();

    return userRepository
        .findByProviderAndAuthSub(provider, providerUserId)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No app user exists for this session."));
  }

  /**
   * Contains the user profile information returned by the service.
   *
   * @param userId     the database user ID
   * @param username   the display name
   * @param bio        the profile biography
   * @param profilePic the profile picture link
   * @param isAdmin    whether the user has administrator access
   * @param email      the stored email address
   */
  public record UserResponse(
      Long userId,
      String username,
      String bio,
      String profilePic,
      boolean isAdmin,
      String email) {
  }
}