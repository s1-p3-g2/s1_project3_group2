package org.example.backend.rating;

import org.example.backend.rating.dtos.DuckRankingResponse;
import org.example.backend.rating.dtos.RatingRequest;
import org.example.backend.rating.dtos.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Make sure this lines up with openapi.yml

/**
 * REST controller for creating, reading, updating, and deleting ratings.
 *
 * <p>More Information on controllers:
 *  <a href="https://www.geeksforgeeks.org/advance-java/spring-boot-controller-annotation-with-example/">
 *  GeeksForGeeks: Spring Boot Controllers</a>
 *  <a href="https://www.baeldung.com/spring-controllers">Baeldung: Spring Boot Controllers</a></p>
 */
@RestController
@RequestMapping("api/v1/ratings")
public class RatingController {
  private final RatingService ratingService;

  /**
   * Creates a rating controller backed by the supplied service.
   *
   * @param ratingService service used to process rating requests
   */
  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  /**
   * Gets a paginated list of ratings.
   *
   * @param pageable pagination and sorting information
   * @return a page containing rating responses
   */
  @GetMapping
  public Page<RatingResponse> getRatings(Pageable pageable) {
    return ratingService.getRatings(pageable);
  }

  /**
   * Creates a rating from the supplied request.
   *
   * @param request rating data to create
   * @return the created rating
   */
  @PostMapping
  public RatingResponse createRating(@RequestBody RatingRequest request) {
    return ratingService.createRating(request);
  }

  /**
   * Updates an existing rating using the supplied request data.
   *
   * @param request rating data containing the user, duck, and positive value
   * @return the updated rating
   */
  @PutMapping
  public RatingResponse updateRating(@RequestBody RatingRequest request) {
    return ratingService.changeRating(request);
  }

  /**
   * Deletes a rating identified by a duck and a user.
   *
   * @param duckId identifier of the duck being rated
   * @param userId identifier of the user who created the rating
   */
  @DeleteMapping("/ducks/{duckId}/users/{userId}")
  public void deleteRating1(@PathVariable int duckId, @PathVariable int userId) {
    ratingService.deleteRating(userId, duckId);
  }

  @DeleteMapping("/users/{userId}/ducks/{duckId}")
  public void deleteRating2(@PathVariable int duckId, @PathVariable int userId) {
    ratingService.deleteRating(userId, duckId);
  }

  /**
   * Gets all ratings for a duck.
   *
   * @param duckId identifier of the duck
   * @param pageable pagination and sorting information
   * @return a page containing ratings for the duck
   */
  @GetMapping("/ducks/{duckId}")
  public Page<RatingResponse> getRatingsByDuck(@PathVariable int duckId, Pageable pageable) {
    return ratingService.getRatingsByDuck(duckId, pageable);
  }

  /**
   * Gets all ratings created by a user.
   *
   * @param userId identifier of the user
   * @param pageable pagination and sorting information
   * @return a page containing ratings created by the user
   */
  @GetMapping("/users/{userId}")
  public Page<RatingResponse> getRatingsByUser(@PathVariable int userId, Pageable pageable) {
    return ratingService.getRatingsByUser(userId, pageable);
  }

  /**
   * Gets one rating for a specific duck and user.
   *
   * @param duckId identifier of the duck
   * @param userId identifier of the user
   * @return the matching rating
   */
  @GetMapping("/ducks/{duckId}/users/{userId}")
  public RatingResponse getRating1(@PathVariable int duckId, @PathVariable int userId) {
    return ratingService.getRating(userId, duckId);
  }

  @GetMapping("/users/{userId}/ducks/{duckId}")
  public RatingResponse getRating2(@PathVariable int duckId, @PathVariable int userId) {
    return ratingService.getRating(userId, duckId);
  }

  /**
   * Gets the ranking information for a duck.
   *
   * @param duckId identifier of the duck
   * @return the duck's rating score and ranking position
   */
  @GetMapping("/rank/{duckId}")
  public DuckRankingResponse getRanking(@PathVariable int duckId) {
    return ratingService.getDuckRanking(duckId);
  }

  /**
   * Gets the authenticated user's rating for a duck.
   *
   * @param duckId identifier of the duck
   * @param auth authentication information for the current user
   * @return the user's rating for the duck
   */
  @GetMapping("/me/{duckId}")
  public RatingResponse getMyRating(@PathVariable int duckId, Authentication auth) {
    return ratingService.getMyRating(duckId, auth);
  }

  // TODO: Revisit when oauth is implemented
  /**
   * Gets a paginated list of ratings created by the authenticated user.
   *
   * @param auth authentication information for the current user
   * @param pageable pagination and sorting information
   * @return a page containing the user's ratings
   */
  @GetMapping("/me")
  public Page<RatingResponse> getMyRatings(Authentication auth, Pageable pageable) {
    return ratingService.getMyRatings(auth, pageable);
  }

  // TODO: Revisit when oauth is implemented
  /**
   * Creates a rating for a duck on behalf of the authenticated user.
   *
   * @param duckId identifier of the duck
   * @param isPositive whether the rating is positive
   * @param auth authentication information for the current user
   * @return the created rating
   */
  @PostMapping("/me/{duckId}")
  public RatingResponse makeMyRating(
      @PathVariable int duckId,
      @RequestBody boolean isPositive,
      Authentication auth
  ) {
    return ratingService.makeMyRating(duckId, auth, isPositive);
  }

  // TODO: Revisit when oauth is implemented

  /**
   * Deletes the authenticated user's rating for a duck.
   *
   * @param duckId identifier of the duck
   * @param auth authentication information for the current user
   */
  @DeleteMapping("/me/{duckId}")
  public void deleteMyRating(@PathVariable int duckId, Authentication auth) {
    ratingService.deleteMyRating(duckId, auth);
  }

  // TODO: Revisit when oauth is implemented
  /**
   * Changes the authenticated user's rating for a duck.
   *
   * @param duckId identifier of the duck
   * @param isPositive the desired positive value for the rating
   * @param auth authentication information for the current user
   * @return the updated rating
   */
  @PutMapping("/me/{duckId}")
  public RatingResponse changeMyRating(
      @PathVariable int duckId,
      @RequestBody boolean isPositive,
      Authentication auth
  ) {
    return ratingService.changeMyRating(duckId, auth, isPositive);
  }
}
