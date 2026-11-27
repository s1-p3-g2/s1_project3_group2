package org.example.backend.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a stored app user and their OAuth provider identity.
 *
 * <p>Description: This is the Model class
 *
 * @author Victoria Ha
 * @version 0.1.1
 * @since 11/27/2026
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@Entity
@Table(name = "users")
public class User {

  /*
   * PMD WARNING: AvoidFieldNameMatchingMethodName
   * https://docs.pmd-code.org/pmd-doc-7.24.0/pmd_rules_java_errorprone.html#avoidfieldnamematchingmethodname
   * Doesn't like User.isAdmin(), idc
   */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long userId;

  @Column(nullable = false, length = 100)
  private String username;

  @Column(nullable = false, length = 10)
  private String provider;

  // oh my fucking god. there was a semicolon here and nobody yelled at me for it
  @Column(name = "provider_user_id", nullable = false, length = 255)
  private String authSub;

  @Column(length = 250)
  private String bio;

  @Column(name = "profile_picture_link", columnDefinition = "TEXT")
  private String profilePic;

  @Column(name = "is_admin", nullable = false)
  private boolean isAdmin;

  @Column(name = "email", nullable = false)
  private String email;

  /**
   * Creates a user with the supplied profile and no administrator access.
   *
   * @param username   the initial display name
   * @param provider   the OAuth provider
   * @param authSub    the provider-specific user ID
   * @param bio        the profile biography
   * @param profilePic the profile picture link
   * @param email      the verified email address
   */
  protected User(
      String username,
      String provider,
      String authSub,
      String bio,
      String profilePic,
      String email) {
    this.username = username;
    this.provider = provider;
    this.authSub = authSub;
    this.bio = bio;
    this.profilePic = profilePic;
    this.isAdmin = false;
    this.email = email;
  }

  /**
   * Creates an empty user for persistence framework use.
   */
  protected User() {
  }

  /**
   * Returns the app's database user ID.
   *
   * @return the database user ID
   */
  public long getUserId() {
    return userId;
  }

  /**
   * Returns the provider-specific user ID.
   *
   * @return the provider-specific user ID
   */
  public String getAuthSub() {
    return authSub;
  }

  /**
   * Returns the profile biography.
   *
   * @return the biography
   */
  public String getBio() {
    return bio;
  }

  /**
   * Returns the display name.
   *
   * @return the display name
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the display name.
   *
   * @param username the new display name
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the OAuth provider.
   *
   * @return the provider name
   */
  public String getProvider() {
    return provider;
  }

  /**
   * Sets the OAuth provider.
   *
   * @param provider the provider name
   */
  public void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   * Sets the provider-specific user ID.
   *
   * @param authSub the provider-specific user ID
   */
  public void setAuthSub(String authSub) {
    this.authSub = authSub;
  }

  /**
   * Sets the profile biography.
   *
   * @param bio the new biography
   */
  public void setBio(String bio) {
    this.bio = bio;
  }

  /**
   * Returns the profile picture link.
   *
   * @return the profile picture link
   */
  public String getProfilePic() {
    return profilePic;
  }

  /**
   * Sets the profile picture link.
   *
   * @param profilePic the new profile picture link
   */
  public void setProfilePic(String profilePic) {
    this.profilePic = profilePic;
  }

  /**
   * Returns whether the user has administrator access.
   *
   * @return true if the user is an administrator
   */
  public boolean isAdmin() {
    return isAdmin;
  }

  /**
   * Sets the user's administrator status.
   *
   * @param admin whether the user should have administrator access
   */
  // package private just so that other files can't call it willy nilly
  void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  /**
   * Returns the stored email address.
   *
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the stored email address.
   *
   * @param email the new email address
   */
  public void setEmail(String email) {
    this.email = email;
  }
}