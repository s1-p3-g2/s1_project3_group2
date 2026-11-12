package org.example.backend.rating;

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

@RestController
@RequestMapping("api/v1/ratings")
public class RatingController {
  private final RatingService ratingService;

  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @GetMapping
  public Page<RatingResponse> getRatings(Pageable pageable) {
    return ratingService.getRatings(pageable);
  }

  @PostMapping
  public RatingResponse createRating(@RequestBody RatingRequest request) {
    return ratingService.createRating(request);
  }

  // Uses request body to update rating, should include user_id, duck_id, and isPositive
  // No need for patch, all fields are required to edit a rating
  @PutMapping
  public RatingResponse updateRating(@RequestBody RatingRequest request) {
    return ratingService.changeRating(request);
  }

  @DeleteMapping("/ducks/{duckId}/users/{userId}")
  public void deleteRating(@PathVariable int duckId, @PathVariable int userId) {
    ratingService.deleteRating(userId, duckId);
  }

  @GetMapping("/ducks/{duckId}")
  public Page<RatingResponse> getRatingsByDuck(@PathVariable int duckId, Pageable pageable) {
    return ratingService.getRatingsByDuck(duckId, pageable);
  }

  @GetMapping("/users/{userId}")
  public Page<RatingResponse> getRatingsByUser(@PathVariable int userId, Pageable pageable) {
    return ratingService.getRatingsByUser(userId, pageable);
  }

  @GetMapping("/ducks/{duckId}/users/{userId}")
  public RatingResponse getRating(@PathVariable int duckId, @PathVariable int userId) {
    return ratingService.getRating(userId, duckId);
  }

  @GetMapping("/me/{duckId}")
  public RatingResponse getMyRating(@PathVariable int duckId, Authentication auth) {
    return ratingService.getMyRating(duckId, auth);
  }

  // TODO: Revisit when oauth is implemented
  @GetMapping("/me")
  public Page<RatingResponse> getMyRatings(Authentication auth, Pageable pageable) {
    return ratingService.getMyRatings(auth, pageable);
  }

  // TODO: Revisit when oauth is implemented
  @PostMapping("/me/{duckId}")
  public RatingResponse makeMyRating(@PathVariable int duckId, @RequestBody boolean isPositive, Authentication auth) {
    return ratingService.makeMyRating(duckId, auth, isPositive);
  }

  // TODO: Revisit when oauth is implemented
  @DeleteMapping("/me/{duckId}")
  public void deleteMyRating(@PathVariable int duckId, Authentication auth) {
    ratingService.deleteMyRating(duckId, auth);
  }

  // TODO: Revisit when oauth is implemented
  @PutMapping("/me/{duckId}")
  public RatingResponse changeMyRating(@PathVariable int duckId, @RequestBody boolean isPositive, Authentication auth) {
    return ratingService.changeMyRating(duckId, auth, isPositive);
  }
}
