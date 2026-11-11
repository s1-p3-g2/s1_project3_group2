package org.example.backend.rating;


import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Paul Conner
 * @since
 *
 **/

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
  Page<Rating> findByDuck_DuckId(int duckId, Pageable pageable);

  Page<Rating> findByUser_UserId(int userId, Pageable pageable);

  Optional<Rating> findByDuck_DuckIdAndUser_UserId(int duckId, int userId);
}
