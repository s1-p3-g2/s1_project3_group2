package org.example.backend.rating;


import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RatingRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:16-alpine")
          .withDatabaseName("ratemyduck")
          .withUsername("test")
          .withPassword("test");

  @Autowired
  RatingRepository ratingRepository;

  // Used for verify deletions
  @Autowired
  TestEntityManager tem; // Hoi!

  // Predefined list of ratings used to setup db with values
  List<Rating> ratings = new ArrayList<>(List.of(
      new Rating(1, 1, false),
      new Rating(1, 2, true),
      new Rating(1, 3, true),
      new Rating(2, 2, true),
      new Rating(3, 1, false),
      new Rating(4, 3, false)
  ));

  int pageNum = 0;
  int pageSize = 2;
  Pageable pageable;

  @BeforeEach
  void setUp() {
    // Clears existing database to ensure consistency
    ratingRepository.deleteAll();

    ratingRepository.saveAll(ratings);

    // Uses this format for all pages in tests
    pageable = PageRequest.of(pageNum, pageSize, Sort.by("ratingId.userId").ascending());
  }

  @Test
  void saveRatingTest() {
    Rating rating = new Rating(5, 5, false);
    Optional<Rating> saveResult = Optional.of(ratingRepository.save(rating));
    assertTrue("Save rating returned a value", saveResult.isPresent()); // Prob redundant
    assertEquals("Saved rating matches input rating", rating, saveResult.get());
  }

  // This one is weird, already calls save all, so they should be retrievable
  @Test
  void saveAllTest() {
    List<Rating> foundRatings = ratingRepository.findAll();
    assertTrue("Save all ratings is equal to list that filled it", foundRatings.equals(ratings));
  }

  @Test
  void findRatingBySeparateIdsRatingTest() {
    Rating rating = ratings.get(5);
    int userId = rating.getRatingId().getUserId();
    int duckId = rating.getRatingId().getDuckId();

    Optional<Rating> result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(
        duckId,
        userId
    );

    assertTrue("Rating lookup returned an object", result.isPresent());
    assertEquals("Rating lookup returned correct object", rating, result.get());
    assertEquals("Rating lookup object has correct id", rating.getRatingId(),
        result.get().getRatingId());
  }

  @Test
  void findRatingByCompoundIdRatingTest() {
    Rating rating = ratings.get(5);
    RatingId id = rating.getRatingId();

    Optional<Rating> result = ratingRepository.findByRatingId(id);
    assertTrue("Rating lookup returned an object", result.isPresent());
    assertEquals("Rating lookup returned correct object", rating, result.get());
    assertEquals("Rating lookup object has correct id", id, result.get().getRatingId());

  }

  @Test
  void findAllRatingsTest() {
    Page<Rating> page = ratingRepository.findAll(pageable);

    assertTrue("Found rating has content", page.hasContent());
    // Get total elements returns as a long
    assertEquals("Rating page lookup has correct total number of ratings", (long)ratings.size(), page.getTotalElements());
    assertEquals("Rating page lookup has correct size", pageSize, page.getContent().size());
    assertEquals("Found rating page has correct page number", pageNum, page.getNumber());
  }

  @Test
  void findRatingByUserIdTest() {
    Page<Rating> page = ratingRepository.findByRatingId_UserId(1, pageable);

    assertTrue("Found rating has content", page.hasContent());
    // Get total elements returns as a long
    assertEquals("Rating page lookup has correct total number of ratings", 3L, page.getTotalElements());
    assertEquals("Rating page lookup has correct size", pageSize, page.getContent().size());
    assertEquals("Found rating page has correct page number", pageNum, page.getNumber());
  }

  @Test
  void findRatingByDuckIdTest() {
    Page<Rating> page = ratingRepository.findByRatingId_DuckId(1, pageable);

    assertTrue("Found rating has content", page.hasContent());
    // Get total elements returns as a long
    assertEquals("Rating page lookup has correct total number of ratings", 2L, page.getTotalElements());
    assertEquals("Rating page lookup has correct size", pageSize, page.getContent().size());
    assertEquals("Found rating page has correct page number", pageNum, page.getNumber());
  }

  // Deletion tests credit to:
  // https://stackoverflow.com/questions/57235922/junit-how-to-test-method-which-deletes-entity-jpa

  @Test
  void deleteRatingByCompoundIdTest() {
    Rating rating = new Rating(5,5,true);
    final RatingId id = tem.persistAndGetId(rating, RatingId.class);
    ratingRepository.deleteByRatingId(rating.getRatingId());
    tem.flush();
    Rating after = tem.find(Rating.class, id);
    assertNull("Rating delete makes rating null", after);
  }

  @Test
  void deleteRatingBySeparateIdsTest() {
    Rating rating = new Rating(5,5,true);
    final RatingId id = tem.persistAndGetId(rating, RatingId.class);
    ratingRepository.deleteByRatingId_DuckIdAndRatingId_UserId(5,5);
    tem.flush();
    Rating after = tem.find(Rating.class, id);
    assertNull("Rating delete makes rating null", after);
  }

  @Test
  void getRatingScoreTest(){
    int score1 = ratingRepository.getDuckRatingScore(1).orElse(0);
    int score2 = ratingRepository.getDuckRatingScore(2).orElse(0);
    int score3 = ratingRepository.getDuckRatingScore(3).orElse(0);
    assertEquals("Rating score on duck 1 correctness", -2, score1);
    assertEquals("Rating score on duck 2 correctness", 2, score2);
    assertEquals("Rating score on duck 3 correctness", 0, score3);
  }

  @Test
  void getDuckPositionTest(){
    int position1 = ratingRepository.getDuckRank(1).orElse(-1);
    int position2 = ratingRepository.getDuckRank(2).orElse(-1);
    int position3 = ratingRepository.getDuckRank(3).orElse(-1);
    assertEquals("Rating position on duck 1 correctness", 3, position1);
    assertEquals("Rating position on duck 2 correctness", 1, position2);
    assertEquals("Rating position on duck 3 correctness", 2, position3);
  }
}
