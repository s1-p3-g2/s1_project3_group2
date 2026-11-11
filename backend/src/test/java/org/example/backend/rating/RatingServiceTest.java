package org.example.backend.rating;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.backend.rating.dtos.DuckRankingResponse;
import org.example.backend.rating.dtos.RatingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.AssertionErrors;

// Source: https://stackoverflow.com/questions/75482134/generating-unit-tests-for-my-service-implementations-on-the-spring-boot-applicat

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

  @InjectMocks
  private RatingService ratingService;

  @Mock
  private RatingRepository ratingRepository;

  @Test
  void getRatingTest() {
    Rating rating = new Rating(1, 1, false);
    RatingResponse response = new RatingResponse(1, 1, false);

    when(ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(1, 1))
        .thenReturn(Optional.of(rating));

    RatingResponse result = ratingService.getRating(1, 1);

    assertEquals(response, result, "getRating check");

    verify(ratingRepository)
        .findByRatingId_DuckIdAndRatingId_UserId(1, 1);

    // Tests non-existent get
    assertThrows(
        RuntimeException.class,
        () -> ratingService.getRating(1, 2)
    );
  }

  @Test
  void getRatingsTest() {
    Pageable pageable = PageRequest.of(0, 2, Sort.by("ratingId.userId").ascending());

    List<Rating> ratings = List.of(
        new Rating(1, 1, false),
        new Rating(2, 2, false)
    );

    List<RatingResponse> responses = List.of(
        new RatingResponse(1, 1, false),
        new RatingResponse(2, 2, false)
    );

    Page<Rating> page = new PageImpl<>(ratings, pageable, 2);

    when(ratingRepository.findAll(pageable)).thenReturn(page);

    Page<RatingResponse> result = ratingService.getRatings(pageable);

    assertEquals(2, result.getContent().size());
    assertEquals(2L, result.getTotalElements());
    assertEquals(0, result.getNumber());
    assertEquals(2, result.getSize());
    assertEquals(responses, result.getContent());

    verify(ratingRepository)
        .findAll(pageable);
  }

  @Test
  void getRatingsByDuckTest() {
    Pageable pageable = PageRequest.of(0, 2, Sort.by("ratingId.userId").ascending());

    List<Rating> ratings = List.of(
        new Rating(1, 1, false),
        new Rating(2, 1, false)
    );

    List<RatingResponse> responses = List.of(
        new RatingResponse(1, 1, false),
        new RatingResponse(2, 1, false)
    );

    Page<Rating> page = new PageImpl<>(ratings, pageable, 2);

    when(ratingRepository.findByRatingId_DuckId(1, pageable)).thenReturn(page);

    Page<RatingResponse> result = ratingService.getRatingsByDuck(1, pageable);

    assertEquals(2, result.getContent().size());
    assertEquals(2L, result.getTotalElements());
    assertEquals(0, result.getNumber());
    assertEquals(2, result.getSize());
    assertEquals(responses, result.getContent());

    verify(ratingRepository)
        .findByRatingId_DuckId(1, pageable);
  }

  @Test
  void getRatingsByUserTest() {
    Pageable pageable = PageRequest.of(0, 2, Sort.by("ratingId.userId").ascending());

    List<Rating> ratings = List.of(
        new Rating(2, 1, false),
        new Rating(2, 2, false)
    );

    List<RatingResponse> responses = List.of(
        new RatingResponse(2, 1, false),
        new RatingResponse(2, 2, false)
    );

    Page<Rating> page = new PageImpl<>(ratings, pageable, 2);

    when(ratingRepository.findByRatingId_UserId(2, pageable)).thenReturn(page);

    Page<RatingResponse> result = ratingService.getRatingsByUser(2, pageable);

    assertEquals(2, result.getContent().size());
    assertEquals(2L, result.getTotalElements());
    assertEquals(0, result.getNumber());
    assertEquals(2, result.getSize());
    assertEquals(responses, result.getContent());

    verify(ratingRepository)
        .findByRatingId_UserId(2, pageable);
  }

  @Test
  void getDuckRankingTest() {
    List<Rating> ratings = new ArrayList<>(List.of(
        new Rating(1, 1, false),
        new Rating(1, 2, true),
        new Rating(1, 3, true),
        new Rating(2, 2, true),
        new Rating(3, 1, false),
        new Rating(4, 3, false)
    ));
    ratingRepository.saveAll(ratings);

    DuckRankingResponse response1 = new DuckRankingResponse(1, -2, 3);
    DuckRankingResponse response2 = new DuckRankingResponse(2, 2, 1);
    DuckRankingResponse response3 = new DuckRankingResponse(3, 0, 2);
    DuckRankingResponse response4 = new DuckRankingResponse(3, 0, 2);


    when(ratingRepository.getDuckRatingScore(1))
        .thenReturn(Optional.of(-2));
    when(ratingRepository.getDuckRank(1))
        .thenReturn(Optional.of(3));

    when(ratingRepository.getDuckRatingScore(2))
        .thenReturn(Optional.of(2));
    when(ratingRepository.getDuckRank(2))
        .thenReturn(Optional.of(1));

    when(ratingRepository.getDuckRank(3))
        .thenReturn(Optional.of(2));
    when(ratingRepository.getDuckRatingScore(3))
        .thenReturn(Optional.of(0));


    when(ratingRepository.getDuckRank(4))
        .thenReturn(Optional.of(-1));
    when(ratingRepository.getDuckRatingScore(4))
        .thenReturn(Optional.of(0));

    DuckRankingResponse result1 = ratingService.getDuckRanking(1);
    assertEquals(response1, result1);
    verify(ratingRepository).getDuckRatingScore(1);
    verify(ratingRepository).getDuckRank(1);

    DuckRankingResponse result2 = ratingService.getDuckRanking(2);
    assertEquals(response2, result2);
    verify(ratingRepository).getDuckRatingScore(2);
    verify(ratingRepository).getDuckRank(2);

    DuckRankingResponse result3 = ratingService.getDuckRanking(3);
    assertEquals(response3, result3);
    verify(ratingRepository).getDuckRatingScore(3);
    verify(ratingRepository).getDuckRank(3);

    assertThrows(
        RuntimeException.class,
        () -> ratingService.getDuckRanking(4)
    );
  }

  @Test
  void createRatingTest() {
    Rating rating = new Rating(1, 1, false);
    RatingResponse response = new RatingResponse(1, 1, false);
    when(ratingRepository.save(rating))
        .thenReturn(rating);

    RatingResponse result = ratingService.createRating(rating);

    assertEquals(response, result);

    verify(ratingRepository)
        .save(rating);

    assertThrows(
        RuntimeException.class,
        () -> ratingService.createRating(rating)
    );
  }

  @Test
  void updateRatingTest() {
    ratingRepository.save(new Rating(1, 1, false));

    Rating updatedRating = new Rating(1, 1, true);
    RatingResponse updatedResponse = new RatingResponse(1, 1, true);

    when(ratingRepository.save(updatedRating)).thenReturn(updatedRating);

    RatingResponse response = ratingService.changeRating(updatedRating);

    assertEquals(updatedResponse, response);

    verify(ratingRepository).save(updatedRating);

    assertThrows(
        RuntimeException.class,
        () -> ratingService.changeRating(new Rating(99, 99, false))
    );
  }

  // Feels useless/redundant, but ¯\_(ツ)_/¯
  @Test
  void deleteRatingTest() {
    when(ratingRepository.deleteByRatingId_DuckIdAndRatingId_UserId(1,1))
        .thenReturn(1L);

    when(ratingRepository.deleteByRatingId_DuckIdAndRatingId_UserId(99,99))
        .thenReturn(0L);

    assertDoesNotThrow(() ->
        ratingService.deleteRating(1, 1)
    );

    assertThrows(
        RuntimeException.class,
        () -> ratingService.deleteRating(99,99)
    );

    verify(ratingRepository, times(1)).deleteByRatingId_DuckIdAndRatingId_UserId(1,1);
    verify(ratingRepository, times(1)).deleteByRatingId_DuckIdAndRatingId_UserId(99,99);
  }
}
