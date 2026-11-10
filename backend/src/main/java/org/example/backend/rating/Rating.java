package org.example.backend.rating;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name = "ratings")
public class Rating {

  @EmbeddedId
  private RatingId ratingId;

  /* TODO: Reimplement when these are implemented
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("userId")
  @JoinColumn(name="user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("duckId")
  @JoinColumn(name="duck_id", nullable = false)
  private Duck duck;
   */
    @Column(name = "user_id", nullable = false)
    private int userId;

    public Rating(int userId, int duckId, boolean isPositive) {
        this.ratingId = new RatingId(userId, duckId);
        this.userId = userId;
        this.duckId = duckId;
        this.isPositive = isPositive;
    }

    @Column(name = "duck_id", nullable = false)
  private int duckId;

  @Column(name = "isPositive", nullable = false)
  private boolean isPositive;

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
}
