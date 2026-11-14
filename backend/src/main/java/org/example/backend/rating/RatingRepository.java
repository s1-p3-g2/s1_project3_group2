package org.example.backend.rating;

import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * A repository, inheriting declarations from {@link JpaRepository}.
 * Accesses the ratings table present in the database
 */
@SuppressWarnings("checkstyle:methodname") // Doesnt like derived queries
@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {

  // Doesn't need to be here, but nice to see syntax
  /**
   * Retrieves a {@link Page} of {@link Rating} objects without a filter,
   * limited by the size defined in the given {@link Pageable}.
   *
   * <p>Method inherited via {@link org.springframework.data.repository.CrudRepository}</p>
   *
   * @param pageable The Spring Boot {@link Pageable} that limits how many rows are retrieved
   * @return A {@link Page} of {@link Rating} objects
   */
  @Override
  Page<Rating> findAll(@NonNull Pageable pageable);

  /**
   * Finds a single rating in the database by the embedded primary key defined in {@link Rating}.
   *
   * <p>DB Query is derived by Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html</p>
   *
   * @param id An {@link jakarta.persistence.Embeddable} class,
   *           which functions as a composite primary key for a rating
   * @return An {@link Optional} containing either a {@link Rating} or nothing
   */
  Optional<Rating> findByRatingId(RatingId id);

  /**
   * Uses a duck_id and user_id in the ratings table
   *        to identify and delete a specific {@link Rating}.
   *
   * <p>DB Query is derived by Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html</p>
   *
   * @param duckId Represents the database id for a single duck
   * @param pageable The Spring Boot {@link Pageable} that limits how many rows are retrieved
   * @return A {@link Page} of {@link Rating} objects
   */
  Page<Rating> findByRatingId_DuckId(int duckId, Pageable pageable);

  /**
   * Uses user_id in the ratings table to find {@link Rating} objects via the users who made them.
   *
   * <p>DB Query is derived by Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html</p>
   *
   * @param userId Represents the database id for a single user
   * @param pageable The Spring Boot {@link Pageable} that limits how many rows are retrieved
   * @return A {@link Page} of {@link Rating} objects
   */
  Page<Rating> findByRatingId_UserId(int userId, Pageable pageable);

  /**
   * Uses a ratings duck_id and user_id to find a specific {@link Rating}.
   *
   * @param duckId Represents the database id for a single duck
   * @param userId Represents the database id for a single user
   * @return An {@link Optional} containing either a {@link Rating} or nothing
   */
  Optional<Rating> findByRatingId_DuckIdAndRatingId_UserId(int duckId, int userId);

  // Not used/obsolete
  // long deleteByRatingId(RatingId id);

  /**
   * Uses a ratings duck_id and user_id to identify and delete a specific {@link Rating}.
   *
   * @param duckId Represents the database id for a single duck
   * @param userId Represents the database id for a single user
   * @return A long value representing the number of values deleted, should be 1 or 0
   */
  long deleteByRatingId_DuckIdAndRatingId_UserId(int duckId, int userId);

  /**
   * Uses a custom query to obtain a duck's rating
   * difference between positive and negative user ratings using its id (duck_id).
   *
   * @param duckId Represents the database id for a single duck
   * @return A {@link Integer} calculated from the database that
   *        represents the duck's score, # positive ratings - # negative ratings
   */
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

  /**
   * Uses custom query to obtain the duck's relative position using rating score.
   *
   * @param duckId Represents the database id for a single duck
   * @return A {@link Integer} calculated from the database
   *      that represents the duck's position based on its scoring
   */
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
