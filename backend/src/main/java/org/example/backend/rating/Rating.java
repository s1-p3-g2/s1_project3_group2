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

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("userId")
  @JoinColumn(name="user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("duckId")
  @JoinColumn(name="duck_id", nullable = false)
  private Duck duck;

  @Column(name = "isPositive", nullable = false)
  private boolean isPositive;

}
