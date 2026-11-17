package org.example.backend.rating.dtos;


/**
 * Represents the resulting JSON structure returned when calling rating-related api requests.
 *
 * @param userId Represents database id for a single user
 * @param duckId Represents the database id for a single duck
 * @param isPositive Whether the rating was positive
 */
public record RatingResponse(
    int userId,
    int duckId,
    boolean isPositive
) {}
