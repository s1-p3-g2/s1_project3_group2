package org.example.backend.rating;


import java.util.List;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Paul Conner
 * @since
 *
 **/

public class RatingService {
  private RatingRepository ratingRepository;

  public RatingService(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
  }

  /**
   * Gets a page of ratings
   * @param pageable
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatings( Pageable pageable) throws NotImplementedException {
    throw new NotImplementedException("Implement getRatings");
  }


  /**
   * Gets a page of ratings for a user
   * @param userId 1-indexed, an user_id from the user table
   * @throws NotImplementedException Not Implemented
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatingsByUser( int userId, Pageable pageable) throws NotImplementedException {
    throw new NotImplementedException("Implement getRatingsByUser");
  }

  /**
   * Gets a page of ratings for a duck
   * @param duckId 1-indexed, a duck_id from the duck table
   * @throws NotImplementedException Not Implemented
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Page<Rating> getRatingsByDuck( int duckId, Pageable pageable) throws NotImplementedException {
    throw new NotImplementedException("Implement getRatingsByDuck");
  }

  /**
   * Gets a singular rating based on supplied user and duck ids
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   * @throws NotImplementedException Not Implemented
   */
  // TODO: Implement this
  // TODO: Think about returning a Rating or new rating response to gate information from users
  @Transactional(readOnly = true)
  public Rating getRating(int userId, int duckId) throws NotImplementedException {
    throw new NotImplementedException("Implement getRating");
  }

  /**
   * Deletes singular rating based on supplied user and duck ids
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   * @throws NotImplementedException Not Implemented
   */
  // TODO: Implement this
  @Transactional
  public void deleteRating(int userId, int duckId) throws NotImplementedException {
    throw new NotImplementedException("Implement deleteRating");
  }

  /**
   * Creates a rating based on supplied information
   * @param rating Rating to be created, contains user_id, duck_id, and isPositive
   * @throws NotImplementedException Not Implemented
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  @Transactional
  public Rating createRating(Rating rating) throws NotImplementedException {
    throw new NotImplementedException("Implement createRating");
  }

  /**
   * Changes a rating based on supplied information
   * @param rating Rating to be changed, contains user_id, duck_id, and isPositive
   * @throws NotImplementedException Not Implemented
   */
  @Transactional
  public Rating changeRating(Rating rating) throws NotImplementedException {
    throw new NotImplementedException("Implement changeRating");
  }

  /**
   * Views ratings based on supplied auth
   * @throws NotImplementedException
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public Page<Rating> getMyRating(int userId, int duckId, Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Implement getMyRating");
  }

  /**
   * Views ratings based on supplied auth
   * @throws NotImplementedException
   */
  // TODO: Implement this
  // TODO: Think about returning Ratings or new rating response to gate information from users
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public Page<Rating> getMyRatings(Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Implement getMyRatings");
  }

  /**
   * Changes a rating based on supplied auth and duck id
   * @throws NotImplementedException
   */
  // TODO: Implement this
  // TODO: Revisit when oauth is implemented
  @Transactional
  public Rating changeMyRating(int duckId, Authentication auth, boolean isPositive) throws NotImplementedException {
    throw new NotImplementedException("Implement changeMyRating");
  }

  /**
   * Deletes a rating based on supplied auth and duck id
   * @throws NotImplementedException
   */
  // TODO: Implement this
  // TODO: Revisit when oauth is implemented
  @Transactional
  public Rating deleteMyRating(int duckId, Authentication auth, boolean isPositive) throws NotImplementedException {
    throw new NotImplementedException("Implement deleteMyRating");
  }

  /**
   * Makes a rating based on supplied auth and duck id
   * @throws NotImplementedException
   */
  // TODO: Implement this
  // TODO: Revisit when oauth is implemented
  @Transactional
  public Rating makeMyRating(int duckId, Authentication auth, boolean isPositive) throws NotImplementedException {
    throw new NotImplementedException("Implement makeMyRating");
  }
}
