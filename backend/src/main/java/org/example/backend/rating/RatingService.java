package org.example.backend.rating;

import org.example.backend.rating.dtos.DuckRankingResponse;
import org.example.backend.rating.dtos.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend.utils.UserUtils;

// TODO: review error handling at a later point

@Service
public class RatingService {

  private final RatingRepository ratingRepository;

  public RatingService(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  /**
   * Gets a page of ratings
   *
   * @param pageable
   */
  @Transactional(readOnly = true)
  public Page<RatingResponse> getRatings(Pageable pageable) {
    return ratingRepository.findAll(pageable)
        .map(this::ratingResponseInit);
  }


  /**
   * Gets a page of ratings for a user
   *
   * @param userId 1-indexed, an user_id from the user table
   */
  @Transactional(readOnly = true)
  public Page<RatingResponse> getRatingsByUser(int userId, Pageable pageable) {
    return ratingRepository.findByRatingId_UserId(userId, pageable)
        .map(this::ratingResponseInit);
  }

  /**
   * Gets a page of ratings for a duck
   *
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  @Transactional(readOnly = true)
  public Page<RatingResponse> getRatingsByDuck(int duckId, Pageable pageable) {
    return ratingRepository.findByRatingId_DuckId(duckId, pageable)
        .map(this::ratingResponseInit);
  }

  /**
   * Gets a singular rating based on supplied user and duck ids
   *
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  @Transactional(readOnly = true)
  public RatingResponse getRating(int userId, int duckId) {
    return ratingResponseInit(ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException("getRating: Requested rating doesnt exist")
        ));
  }

  /**
   * Deletes singular rating based on supplied user and duck ids
   *
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table Not Implemented
   */
  @Transactional
  public void deleteRating(int userId, int duckId) {
    ratingRepository.deleteByRatingId_DuckIdAndRatingId_UserId(duckId, userId);
  }

  /**
   * Creates a rating based on supplied information
   *
   * @param rating Rating to be created, contains user_id, duck_id, and isPositive
   */

  @Transactional
  public RatingResponse createRating(Rating rating) {
    return ratingResponseInit(ratingRepository.save(rating));
  }

  /**
   * Changes a rating based on supplied information
   *
   * @param rating Rating to be changed, contains user_id, duck_id, and isPositive
   */
  @Transactional
  public RatingResponse changeRating(Rating rating) {
    Rating result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(
            rating.getRatingId().getDuckId(),
            rating.getRatingId().getUserId()
        )
        .orElseThrow(() ->
            new RuntimeException("changeRating: Requested rating does not exist")
        );
    // Flips boolean value of rating, can only change boolean
    result.setPositive(!rating.isPositive());
    return ratingResponseInit(ratingRepository.save(result));
  }

  /**
   * Views ratings based on supplied auth
   */
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public RatingResponse getMyRating(int duckId, Authentication auth) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    return ratingResponseInit(ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException("getMyRating: Requested rating does not exist")
        ));
  }

  /**
   * Views ratings based on supplied auth
   */
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public Page<RatingResponse> getMyRatings(Authentication auth, Pageable pageable) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    // Uses custom function to replace instances of Ratings with RatingResponses
    return ratingRepository.findByRatingId_UserId(userId, pageable)
        .map(this::ratingResponseInit);
  }

  /**
   * Changes a rating based on supplied auth and duck id
   */
  // TODO: Revisit when oauth is implemented
  @Transactional
  public RatingResponse changeMyRating(int duckId, Authentication auth, boolean isPositive) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    Rating result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException(
                "changeMyRating: Unknown error during findByRatingId_DuckIdAndRatingId_UserId")
        );
    if (result.isPositive() == isPositive) {
      throw new RuntimeException(
          "changeMyRating: Desired rating is the same as the current rating");
    }
    result.setPositive(isPositive);
    return ratingResponseInit(ratingRepository.save(result));
  }

  /**
   * Deletes a rating based on supplied auth and duck id
   */
  // TODO: Revisit when oauth is implemented
  @Transactional
  public void deleteMyRating(int duckId, Authentication auth) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    Rating result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException(
                "deleteMyRating: Unknown repository error during findByRatingId_DuckIdAndRatingId_UserId")
        );
    ratingRepository.delete(result);
  }

  /**
   * Makes a rating based on supplied auth and duck id
   */
  // TODO: Revisit when oauth is implemented
  @Transactional
  public RatingResponse makeMyRating(int duckId, Authentication auth, boolean isPositive) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    if (ratingRepository
        .findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .isPresent()) {
      throw new RuntimeException("makeMyRating: Rating already exists");
    }
    // Uses a custom mapping function
    return ratingResponseInit(ratingRepository.save(new Rating(userId, duckId, isPositive)));
  }

  /**
   * Gets overall rating information about a duck
   */
  @Transactional(readOnly = true)
  public DuckRankingResponse getDuckRanking(int duckId) {
    int score = ratingRepository.getDuckRatingScore(duckId).orElseThrow(() ->
        new RuntimeException("getDuckRanking: Unknown repository error during getDuckRatingScore")
    );

    int position = ratingRepository.getDuckRank(duckId).orElseThrow(() ->
        new RuntimeException("getDuckRanking: Unknown repository error during getDuckRank")
    );

    return new DuckRankingResponse(duckId, score, position);
  }

  // Maps rating values to the response values
  private RatingResponse ratingResponseInit(Rating rating) {
    return new RatingResponse(rating.getRatingId().getUserId(), rating.getRatingId().getDuckId(), rating.isPositive());
  }

}