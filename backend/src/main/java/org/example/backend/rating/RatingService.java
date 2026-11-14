package org.example.backend.rating;

import org.example.backend.rating.dtos.DuckRankingResponse;
import org.example.backend.rating.dtos.RatingRequest;
import org.example.backend.rating.dtos.RatingResponse;
import org.example.backend.utils.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: review error handling at a later point

/**
 * The service that provides the base functions used by the Rating's related methods for the API.
 *
 * <p>More Information on services:
 * <a href="https://docs.spring.io/spring-boot/reference/io/webservices.html">Spring Boot Docs:
 * Service</a>
 * <a
 * href="https://www.geeksforgeeks.org/advance-java/spring-boot-service-annotation-with-example/">GeeksForGeeks:
 * Spring Boot Services</a>
 *
 * @author Paul Conner - paullert
 * @since 11/14/2026
 */
@Service
public class RatingService {

  private final RatingRepository ratingRepository;
  //  private final UserRepository userRepository;

  /**
   * Rating service constructor, automatically called by Spring Boot on initialization.
   *
   * @param ratingRepository The database access object that the service uses to interface with the
   *                         database rating table
   */
  public RatingService(RatingRepository ratingRepository) {
    this.ratingRepository = ratingRepository;
    // this.userRepository = userRepository;
  }

  /**
   * Gets a page of ratings.
   *
   * @param pageable A {@link Pageable}, most times automatically passed through by Spring Boot
   * @return A {@link Page} containing {@link RatingResponse} objects
   */
  @Transactional(readOnly = true)
  public Page<RatingResponse> getRatings(Pageable pageable) {
    return ratingRepository.findAll(pageable)
        .map(this::ratingResponseInit);
  }


  /**
   * Gets a page of ratings for a user.
   *
   * @param userId   1-indexed, an user_id from the user table
   * @param pageable A {@link Pageable}, most times automatically passed through by Spring Boot
   * @return A {@link Page} containing {@link RatingResponse} objects
   */
  @Transactional(readOnly = true)
  public Page<RatingResponse> getRatingsByUser(int userId, Pageable pageable) {
    return ratingRepository.findByRatingId_UserId(userId, pageable)
        .map(this::ratingResponseInit);
  }

  /**
   * Gets a page of ratings for a duck.
   *
   * @param duckId   1-indexed, a duck_id from the duck table
   * @param pageable A {@link Pageable}, most times automatically passed through by Spring Boot
   * @return A {@link Page} containing {@link RatingResponse} objects
   */
  @Transactional(readOnly = true)
  public Page<RatingResponse> getRatingsByDuck(int duckId, Pageable pageable) {
    return ratingRepository.findByRatingId_DuckId(duckId, pageable)
        .map(this::ratingResponseInit);
  }

  /**
   * Gets a singular rating based on supplied user and duck ids.
   *
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   * @return The resulting rating information as a {@link RatingResponse} object
   */
  @Transactional(readOnly = true)
  public RatingResponse getRating(int userId, int duckId) {
    return ratingResponseInit(ratingRepository
        .findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException("getRating: Requested rating doesnt exist")
        ));
  }

  /**
   * Deletes singular rating based on supplied user and duck ids.
   *
   * @param userId 1-indexed, a user_id from the user table
   * @param duckId 1-indexed, a duck_id from the duck table
   */
  @Transactional
  public void deleteRating(int userId, int duckId) {
    long numDel = ratingRepository.deleteByRatingId_DuckIdAndRatingId_UserId(duckId, userId);
    if (numDel == 0) {
      throw new RuntimeException("deleteRating: No such rating exists");
    }
  }

  // TODO: handle invalid users and ducks

  /**
   * Creates a rating based on supplied information.
   *
   * @param request The user's {@link RatingRequest} to be added
   * @return The resulting rating information as a {@link RatingResponse} object
   */
  @Transactional
  public RatingResponse createRating(RatingRequest request) {
    if (request.duckId() < 0) {
      throw new RuntimeException("createRating: Invalid duckId, less than zero");
    }
    if (request.userId() < 0) {
      throw new RuntimeException("createRating: Invalid userId, less than zero");
    }
    if (ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(
        request.duckId(),
        request.userId()).isPresent()
    ) {
      throw new RuntimeException("createRating: Invalid request, rating already exists");
    }
    return ratingResponseInit(ratingRepository.save(ratingInit(request)));
  }


  /**
   * Changes a rating based on supplied information.
   *
   * @param request Rating to be changed using a {@link RatingRequest} object
   * @return The resulting rating information as a {@link RatingResponse} object
   */
  @Transactional
  public RatingResponse changeRating(RatingRequest request) {
    if (request.duckId() < 0) {
      throw new RuntimeException("changeRating: Invalid duckId, less than zero");
    }
    if (request.userId() < 0) {
      throw new RuntimeException("changeRating: Invalid userId, less than zero");
    }

    Rating result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(
            request.duckId(),
            request.userId()
        )
        .orElseThrow(() ->
            new RuntimeException("changeRating: Requested rating does not exist")
        );
    // Flips boolean value of rating, can only change boolean
    result.setPositive(request.isPositive());
    return ratingResponseInit(ratingRepository.save(result));
  }

  /**
   * Views ratings based on supplied auth.
   *
   * @param duckId Represents the database id for a single duck
   * @param auth   The JWT Authentication automatically passed
   * @return The resulting rating information as a {@link RatingResponse} object
   */
  // TODO: Revisit when oauth is implemented
  @Transactional(readOnly = true)
  public RatingResponse getMyRating(int duckId, Authentication auth) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    return ratingResponseInit(ratingRepository
        .findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException("getMyRating: Requested rating does not exist")
        ));
  }

  /**
   * Views ratings based on supplied auth.
   *
   * @param auth The JWT Authentication automatically passed
   * @return A {@link Page} containing {@link RatingResponse} objects
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
   * Changes a rating based on supplied auth and duck id.
   *
   * @param duckId     Represents the database id for a single duck
   * @param auth       The JWT Authentication automatically passed
   * @param isPositive The desired approval/disaproval of the duck in question
   * @return The resulting rating information as a {@link RatingResponse} object
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
   * Deletes a rating based on supplied auth and duck id.
   *
   * @param duckId Represents the database id for a single duck
   * @param auth   The JWT Authentication automatically passed
   */
  // TODO: Revisit when oauth is implemented
  @Transactional
  public void deleteMyRating(int duckId, Authentication auth) {
    int userId = UserUtils.getUserIdFromAuth(auth);
    Rating result = ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(duckId, userId)
        .orElseThrow(() ->
            new RuntimeException(
                "deleteMyRating: Unknown repository error during "
                    + "findByRatingId_DuckIdAndRatingId_UserId")
        );
    ratingRepository.delete(result);
  }

  /**
   * Makes a rating based on supplied auth and duck id.
   *
   * @param duckId Represents the database id for a single duck
   * @return The resulting rating information as a {@link RatingResponse} object
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
   * Gets overall rating information about a duck.
   *
   * @param duckId Represents the database id for a single duck
   * @return The resulting duck ranking information returned as a {@link DuckRankingResponse} object
   */
  @Transactional(readOnly = true)
  public DuckRankingResponse getDuckRanking(int duckId) {
    int score = ratingRepository.getDuckRatingScore(duckId).orElseThrow(() ->
        new RuntimeException("getDuckRanking: Unknown repository error during getDuckRatingScore")
    );

    int position = ratingRepository.getDuckRank(duckId).orElseThrow(() ->
        new RuntimeException("getDuckRanking: Unknown repository error during getDuckRank")
    );

    if (position <= 0) {
      throw new RuntimeException("getDuckRanking: Invalid Rank obtained from getDuckRank");
    }

    return new DuckRankingResponse(duckId, score, position);
  }

  /**
   * Creates a new {@link RatingResponse} object using a {@link Rating} object.
   *
   * @param rating The {@link Rating} to be converted
   * @return The resulting {@link RatingResponse} object constructed using a {@link Rating}
   */
  private RatingResponse ratingResponseInit(Rating rating) {
    return new RatingResponse(
        rating.getRatingId().getUserId(),
        rating.getRatingId().getDuckId(),
        rating.isPositive()
    );
  }

  /**
   * Creates a {@link Rating} object using a {@link RatingRequest} object.
   *
   * @param request The {@link RatingRequest} object to be converted
   * @return The resulting {@link Rating} object constructed from a {@link RatingRequest}
   */
  private Rating ratingInit(RatingRequest request) {
    return new Rating(
        request.userId(),
        request.duckId(),
        request.isPositive()
    );
  }
}