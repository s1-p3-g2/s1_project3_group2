package org.example.backend.rating;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Represents the compound-primary key tied to each row of the database.
 */
@Embeddable
public class RatingId {
  @Column(name = "user_id")
  private int userId;
  @Column(name = "duck_id")
  private int duckId;

  /**
   * Basic RatingId constructor.
   *
   * @param userId Represents database id for a single user
   * @param duckId Represents the database id for a single duck
   *
   */
  public RatingId(int userId, int duckId) {
    this.userId = userId;
    this.duckId = duckId;
  }

  /**
   * Parameter-less constructor, here to suppress warnings.
   */
  public RatingId() {}

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getDuckId() {
    return duckId;
  }

  public void setDuckId(int duckId) {
    this.duckId = duckId;
  }

  // Required for repository equality checks
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RatingId other = (RatingId) o;
    return userId == other.userId && duckId == other.duckId;
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(userId, duckId);
  }
}
