package org.example.backend.utils;

import org.springframework.security.core.Authentication;

/**
 * Provides helper methods for accessing user identity from authentication.
 */
public class UserUtils {

  /**
   * Retrieves the user ID from authentication once implemented.
   *
   * @param auth the current request's authentication
   * @return the user ID
   * @throws RuntimeException always, because this method is not implemented
   */
  // TODO: Implement getUserIdFromAuth
  public static int getUserIdFromAuth(Authentication auth) {
    throw new RuntimeException("getUserIdFromAuth not implemented");
  }
}