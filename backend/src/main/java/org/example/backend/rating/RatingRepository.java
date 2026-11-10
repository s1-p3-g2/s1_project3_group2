package org.example.backend.rating;


import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Paul Conner
 * @since
 *
 **/

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
  Page<Rating> findAll(Pageable pageable);

  Page<Rating> findByDuckId(int duckId, Pageable pageable);

  Page<Rating> findByUserId(int userId, Pageable pageable);

  Optional<Rating> findByDuckIdAndUserId(int duckId, int userId);

  long deleteByDuckIdAndUserId(int duckId, int userId);
}
