package java.org.example.backend.rating;

import main.java.org.example.backend.rating.RatingRepository;
import main.java.org.example.backend.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.org.example.backend.rating.Rating;

/**
 * @author Paul Conner
 * @since
 *
 **/

public class RatingService {
  private final RatingRepository ratingRepository;

  public RatingService(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  /**
   * Gets a page of ratings
   * @param pageable
   */
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatings(Pageable pageable) {
    return ratingRepository.findAll(pageable);
  }


  /**
   * Gets a page of ratings for a user
   * @param userId 1-indexed, an user_id from the user table
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatingsByUser( int userId, Pageable pageable) {
    return ratingRepository.findByUserId(userId, pageable);
  }

  /**
   * Gets a page of ratings for a duck
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatingsByDuck( int duckId, Pageable pageable) {
    return ratingRepository.findByDuckId(duckId, pageable);
  }

  /**
   * Gets a singular rating based on supplied user and duck ids
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  // TODO: Implement this
  // TODO: Think about returning a Rating or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Rating getRating(int userId, int duckId) {
    Rating result = ratingRepository.findByDuckIdAndUserId(duckId, userId).orElse(null); // TODO: Handle or else better
    if (result == null) {
      throw new RuntimeException("getRating: Requested rating doesnt exist");
    }
    return result;
  }

  /**
   * Deletes singular rating based on supplied user and duck ids
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
    Not Implemented
   */
  // TODO: Implement this
  @Transactional
  public void deleteRating(int userId, int duckId) {
    long result = ratingRepository.deleteByDuckIdAndUserId(duckId, userId);
    if (result != 1) {
      throw new RuntimeException("deleteRating: Deleted more than the intended row");
    }
  }

  /**
   * Creates a rating based on supplied information
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
   * @param rating Rating to be changed, contains user_id, duck_id, and isPositive
   */
  @Transactional
  public Rating changeRating(Rating rating) {
    Rating result = ratingRepository.findByDuckIdAndUserId(
            rating.getRatingId().getDuckId(),
            rating.getRatingId().getUserId()
    ).orElse(null); // TODO: Handle or else better
    if (result == null) {
      throw new RuntimeException("changeRating: Requested rating does not exist");
    }
    return ratingRepository.save(rating);
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
    Rating result = ratingRepository.findByDuckIdAndUserId(duckId, userId).orElse(null); // TODO: Handle or else better
    if (result == null) {
      throw new RuntimeException("getMyRating: Requested rating does not exist");
    }
    return result;
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
    return ratingRepository.findByUserId(userId, pageable);
  }

  /**
   * Changes a rating based on supplied auth and duck id
   
   */
  // TODO: Implement this
  // TODO: Revisit when oauth is implemented
  @Transactional
  public Rating changeMyRating(int duckId, Authentication auth, boolean isPositive) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    Rating result = ratingRepository.findByDuckIdAndUserId(duckId, userId).orElse(null); // TODO: Handle or else better
    if (result == null){
      throw new RuntimeException("changeMyRating: Unknown error during findByDuckIdAndUserId");
    }
    if (result.isPositive() == isPositive) {
      throw new RuntimeException("Attempted to change rating to already present rating");
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
    Rating result = ratingRepository.findByDuckIdAndUserId(duckId, userId).orElse(null); // TODO: Handle or else better
    if (result == null) {
      throw new RuntimeException("deleteMyRating: Unknown error during findByDuckIdAndUserId");
    }
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
    Rating result = ratingRepository.findByDuckIdAndUserId(duckId, userId).orElse(null); // TODO: Handle or else better
    if (result != null) {
      throw new RuntimeException("makeMyRating: Rating already exists");
    }
    return ratingRepository.save(new Rating(userId, duckId, isPositive));
  }
}