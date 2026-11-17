package org.example.backend.rating;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;
// TODO: Reimplement when they are
// import org.example.backend.duck.Duck;
// import org.example.backend.users.User;

/**
 * Rating Data Entity.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
@Entity
@Table(name = "ratings")
public class Rating {

  // Represents the compound primary key present in database table
  @EmbeddedId
  private RatingId ratingId;

  // @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // @MapsId("userId")
  // @JoinColumn(name="user_id", nullable = false)
  // private User user;

  // @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // @MapsId("duckId")
  // @JoinColumn(name="duck_id", nullable = false)
  // private Duck duck;

  @Column(name = "isPositive", nullable = false)
  private boolean isPositive;

  /* TODO: Revisit this once ducks and users are fully implemented
     @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
     @MapsId("userId")
     @JoinColumn(name = "user_id", nullable = false) private User user;
     @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
     @MapsId("duckId")
     @JoinColumn(name = "duck_id", nullable = false) private Duck duck;
     <p>
     public Rating(User user, Duck duck, boolean isPositive) { this.user = user; this.duck = duck;
     this.ratingId = new RatingId(user.getUserId(), duck.getDuckId()); this.isPositive = isPositive;
     }
   */

  /**
   * Rating constructor that builds the embedded primary key using its base values.
   *
   * @param userId     Represents database id for a single user
   * @param duckId     Represents the database id for a single duck
   * @param isPositive Whether the rating was positive
   */
  public Rating(int userId, int duckId, boolean isPositive) {
    this.ratingId = new RatingId(userId, duckId);
    this.isPositive = isPositive;
  }

  /**
   * Rating constructor that uses an embedded primary key.
   *
   * @param id         The embedded primary key
   * @param isPositive Whether the rating was positive
   */
  public Rating(RatingId id, boolean isPositive) {
    this.ratingId = id;
    this.isPositive = isPositive;
  }

  /**
   * Parameter-less constructor, here to suppress warnings.
   */
  public Rating() {

  }

  public RatingId getRatingId() {
    return ratingId;
  }

  public void setRatingId(RatingId ratingId) {
    this.ratingId = ratingId;
  }

  public void setPositive(boolean positive) {
    isPositive = positive;
  }

  /*
  PMD WARNING: AvoidFieldNameMatchingMethodName ️
    https://docs.pmd-code.org/pmd-doc-7.24.0/pmd_rules_java_errorprone.html#avoidfieldnamematchingmethodname
    Doesn't like Rating.isPostive(), idc
  */
  public boolean isPositive() {
    return isPositive;
  }

  // Needed for comparison logic in repository tests
  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Rating rating = (Rating) o;
    return isPositive() == rating.isPositive() && Objects.equals(getRatingId(),
        rating.getRatingId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRatingId(), isPositive());
  }
}

