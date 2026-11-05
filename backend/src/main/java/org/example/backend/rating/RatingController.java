package org.example.backend.rating;


import jdk.jshell.spi.ExecutionControl;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;
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

/**
 * @author Paul Conner
 * @since
 *
 **/

// TODO: Make sure this lines up with openapi.yml

@RequestMapping("api/v1/ratings")
public class RatingController {
  private RatingService ratingService;

  // TODO: Implement this
  @GetMapping
  public Page<Rating> getRatings(Pageable pageable) throws NotImplementedException {
    throw new NotImplementedException("Finish GET /ratings");
  }

  // TODO: Implement this
  @PostMapping
  public Rating createRating(RatingDTO rating) throws NotImplementedException {
    throw new NotImplementedException("Finish POST /ratings");
  }

  // Uses request body to update rating, should include user_id, duck_id, and isPositive
  // No need for patch, all fields are required to edit a rating
  // TODO: Implement this
  @PutMapping
  public Rating updateRating(@RequestBody RatingDTO rating) throws NotImplementedException {
    throw new NotImplementedException("Finish PUT /ratings");
  }

  // TODO: Implement this
  @DeleteMapping("/ducks/{duckId}/users/{userId}")
  public Rating deleteRating(@PathVariable int duckId, @PathVariable int userId) throws NotImplementedException {
    throw new NotImplementedException("Finish DELETE /ratings/ducks/{duckId}/users/{userId}");
  }

  // TODO: Implement this
  @GetMapping("/ducks/{duckId}")
  public Page<Rating> getRatingsByDuck(@PathVariable int duckId, Pageable pageable) throws NotImplementedException {
    throw new NotImplementedException("Finish GET /ratings/ducks/{duckId}");
  }

  // TODO: Implement this
  @GetMapping("/users/{userId}")
  public Page<Rating> getRatingsByUser(@PathVariable int userId, Pageable pageable) throws NotImplementedException {
    throw new NotImplementedException("Finish GET /ratings/users/{userId}");
  }

  // TODO: Implement this
  @GetMapping("/ducks/{duckId}/users/{userId}")
  public Rating getRating(@PathVariable int duckId, @PathVariable int userId) throws NotImplementedException {
    throw new NotImplementedException("Finish GET /ratings/ducks/{duckId}/users/{userId}");
  }

  // TODO: Implement this
  // TODO: Think about request body more, use DTO or no
  @GetMapping("/me/{duckId}")
  public Rating getMyRating(@PathVariable int duckId, Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Finish GET /ratings/me/{duckId}");
  }

  // TODO: Implement this
  // TODO: Think about request body more, use DTO or no
  @GetMapping("/me")
  public Page<Rating> getMyRatings(Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Finish GET /ratings/me");
  }

  // TODO: Implement this
  // TODO: Think about request body more, use DTO or no
  // TODO: Revisit when oauth is implemented
  @PostMapping("/me/{duckId}")
  public Rating makeMyRating(@PathVariable int duckId, @RequestBody boolean isPositive, Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Finish POST /ratings/me/{duckId}");
  }

  // TODO: Implement this
  // TODO: Think about request body more, use DTO or no
  // TODO: Revisit when oauth is implemented
  @DeleteMapping("/me/{duckId}")
  public Rating deleteMyRating(@PathVariable int duckId, Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Finish DELETE /ratings/me/{duckId}");
  }

  // TODO: Implement this
  // TODO: Think about request body more, use DTO or no
  // TODO: Revisit when oauth is implemented
  @PutMapping("/me/{duckId}")
  public Rating changeMyRating(@PathVariable int duckId, @RequestBody boolean isPositive, Authentication auth) throws NotImplementedException {
    throw new NotImplementedException("Finish PUT /ratings/me/{duckId}");
  }




}
