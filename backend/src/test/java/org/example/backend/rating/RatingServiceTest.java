package org.example.backend.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
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

// Source: https://stackoverflow.com/questions/75482134/generating-unit-tests-for-my-service-implementations-on-the-spring-boot-applicat

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
  @InjectMocks
  private RatingService ratingService;

  @Mock
  private RatingRepository ratingRepository;

  @Test void getRatingExistingTest(){
    Rating rating = new Rating(1,1, false);
    RatingResponse response = new RatingResponse(1,1,false);

    when(ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(1,1))
        .thenReturn(Optional.of(rating));

    RatingResponse result = ratingService.getRating(1,1);

    assertEquals(response, result, "getRating check");

    verify(ratingRepository)
        .findByRatingId_DuckIdAndRatingId_UserId(1,1);
  }

  @Test void getRatingThrowsTest(){
    when(ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(1,1))
        .thenReturn(Optional.empty());

    assertThrows(
        RuntimeException.class,
        () -> ratingService.getRating(1, 1)
    );
  }

  @Test void getRatingsTest(){
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

  @Test void getRatingsByDuckTest(){
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

  @Test void getRatingsByUserTest(){
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


}
