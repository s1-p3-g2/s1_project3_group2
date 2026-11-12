package org.example.backend.rating;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;
import org.example.backend.duck.Duck;

@Entity
@Table(name = "ratings")
public class Rating {

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


  /**
   * TODO: Revisit this once ducks and users are fully implemented
   *
   * @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
   * @MapsId("userId")
   * @JoinColumn(name = "user_id", nullable = false) private User user;
   * @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
   * @MapsId("duckId")
   * @JoinColumn(name = "duck_id", nullable = false) private Duck duck;
   * <p>
   * public Rating(User user, Duck duck, boolean isPositive) { this.user = user; this.duck = duck;
   * this.ratingId = new RatingId(user.getUserId(), duck.getDuckId()); this.isPositive = isPositive;
   * }
   */

  public Rating(int userId, int duckId, boolean isPositive) {
    this.ratingId = new RatingId(userId, duckId);
    this.isPositive = isPositive;
  }

  public Rating(RatingId id, boolean isPositive) {
    this.ratingId = id;
    this.isPositive = isPositive;
  }


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

