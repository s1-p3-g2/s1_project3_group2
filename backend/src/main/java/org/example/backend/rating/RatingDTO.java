package org.example.backend.rating;


/**
 * @author Paul Conner
 * @since
 *
 **/

public record RatingDTO (
    int duckId,
    int userId,
    int isPositive
)
{}
