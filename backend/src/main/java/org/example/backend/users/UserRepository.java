package org.example.backend.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Looks up users by provider identity and checks for email conflicts.
 *
 * <p>Description: this may need fixing down th line but
 *
 * @author Victoria Ha
 * @version 0.1.1
 * @since 11/27/2026
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds the user associated with a provider identity.
   *
   * @param provider the OAuth provider
   * @param authSub  the provider-specific user ID
   * @return the matching user, or an empty Optional if none exists
   */
  Optional<User> findByProviderAndAuthSub(String provider, String authSub);

  /**
   * Checks whether an email belongs to an account under another provider.
   *
   * @param email    the email to compare without regard to capitalization
   * @param provider the provider attempting sign-in
   * @return true if another provider has an account with this email
   */
  boolean existsByEmailIgnoreCaseAndProviderNot(String email, String provider);
}