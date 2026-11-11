package org.example.backend.rating;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend.utils.UserUtils;

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
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatings(Pageable pageable) {
    return ratingRepository.findAll(pageable);
  }


  /**
   * Gets a page of ratings for a user
   *
   * @param userId 1-indexed, an user_id from the user table
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatingsByUser(int userId, Pageable pageable) {
    return ratingRepository.findByRatingId_UserId(userId, pageable);
  }

  /**
   * Gets a page of ratings for a duck
   *
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatingsByDuck(int duckId, Pageable pageable) {
    return ratingRepository.findByRatingId_DuckId(duckId, pageable);
  }

  /**
   * Gets a singular rating based on supplied user and duck ids
   *
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  // TODO: Implement this
  // TODO: Think about returning a Rating or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Rating getRating(int userId, int duckId) {
    return ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException("getRating: Requested rating doesnt exist")
        );
  }

  /**
   * Deletes singular rating based on supplied user and duck ids
   *
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table Not Implemented
   */
  // TODO: Implement this
  @Transactional
  public void deleteRating(int userId, int duckId) {
    ratingRepository.deleteByRatingId_DuckIdAndRatingId_UserId(duckId, userId);
  }

  /**
   * Creates a rating based on supplied information
   *
   * @param rating Rating to be created, contains user_id, duck_id, and isPositive
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional
  public Rating createRating(Rating rating) {
    return ratingRepository.save(rating);
  }

  /**
   * Changes a rating based on supplied information
   *
   * @param rating Rating to be changed, contains user_id, duck_id, and isPositive
   */
  @Transactional
  public Rating changeRating(Rating rating) {
    Rating result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(
            rating.getRatingId().getDuckId(),
            rating.getRatingId().getUserId()
        )
        .orElseThrow(() ->
            new RuntimeException("changeRating: Requested rating does not exist")
        );
    // Flips boolean value of rating, can only change boolean
    result.setPositive(!rating.isPositive());
    return ratingRepository.save(result);
  }

  /**
   * Views ratings based on supplied auth
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public Rating getMyRating(int duckId, Authentication auth) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    return ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException("getMyRating: Requested rating does not exist")
        );
  }

  /**
   * Views ratings based on supplied auth
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public Page<Rating> getMyRatings(Authentication auth, Pageable pageable) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    return ratingRepository.findByRatingId_UserId(userId, pageable);
  }

  /**
   * Changes a rating based on supplied auth and duck id
   */
  // TODO: Implement this
  // TODO: Revisit when oauth is implemented
  @Transactional
  public Rating changeMyRating(int duckId, Authentication auth, boolean isPositive) {
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
    return ratingRepository.save(result);
  }

  /**
   * Deletes a rating based on supplied auth and duck id
   */
  // TODO: Implement this
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
  // TODO: Implement this
  // TODO: Revisit when oauth is implemented
  @Transactional
  public Rating makeMyRating(int duckId, Authentication auth, boolean isPositive) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    if (ratingRepository
        .findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .isPresent()) {
      throw new RuntimeException("makeMyRating: Rating already exists");
    }
    return ratingRepository.save(new Rating(userId, duckId, isPositive));
  }

  @Transactional(readOnly = true)
  public int getDuckScore(int duckId) {
    return ratingRepository.getDuckRatingScore(duckId).orElseThrow(() ->
        new RuntimeException("getDuckScore: Unknown repository error during getDuckRatingScore")
    );
  }

  @Transactional(readOnly = true)
  public int getDuckPosition(int duckId) {
    return ratingRepository.getDuckRank(duckId).orElseThrow(() ->
        new RuntimeException("getDuckPosition: Unknown repository error during getDuckRank")
    );
  }

}