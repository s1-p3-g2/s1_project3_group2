package org.example.backend.rating;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * @author Paul Conner
 * @since
 *
 **/

@Embeddable
public class RatingId {
  @Column(name = "user_id")
  private int userId;
  @Column(name = "duck_id")
  private int duckId;

  public RatingId(int userId, int duckId) {
    this.userId = userId;
    this.duckId = duckId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RatingId other = (RatingId) o;
    return userId == other.userId && duckId == other.duckId;
  }

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
}
