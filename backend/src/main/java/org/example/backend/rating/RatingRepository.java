package org.example.backend.rating;

import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {

  @Override
    // Doesn't need to be here, but nice to see syntax
  Page<Rating> findAll(@NonNull Pageable pageable);

  Optional<Rating> findByRatingId(RatingId id);

  Page<Rating> findByRatingId_DuckId(int duckId, Pageable pageable);

  Page<Rating> findByRatingId_UserId(int userId, Pageable pageable);

  Optional<Rating> findByRatingId_DuckIdAndRatingId_UserId(int duckId, int userId);

  long deleteByRatingId(RatingId id);

  long deleteByRatingId_DuckIdAndRatingId_UserId(int duckId, int userId);

  @Query(value =
      """
         SELECT
            (SELECT COUNT(*) FILTER ( WHERE is_positive = true )) -
            (SELECT COUNT(*) FILTER ( WHERE is_positive = false ))
         FROM ratings
         WHERE duck_id = :duckId
      """,
      nativeQuery = true
  )
  Optional<Integer> getDuckRatingScore(@Param("duckId") int duckId);

  @Query(
      value = """
        SELECT CAST(rank AS INTEGER)
        FROM (
            SELECT
                duck_id,
                DENSE_RANK() OVER (ORDER BY score DESC) AS rank
            FROM (
                SELECT
                    duck_id,
                    SUM(
                        CASE
                            WHEN is_positive THEN 1
                            ELSE -1
                        END
                    ) AS score
                FROM ratings
                GROUP BY duck_id
            ) scores
        ) ranked
        WHERE duck_id = :duckId
        """,
      nativeQuery = true
  )
  Optional<Integer> getDuckRank(@Param("duckId") int duckId);
}


