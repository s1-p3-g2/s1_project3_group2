package org.example.backend.rating.dtos;

import org.example.backend.rating.Rating;

public record RatingResponse(
    int userId,
    int duckId,
    boolean isPositive
) {

}
