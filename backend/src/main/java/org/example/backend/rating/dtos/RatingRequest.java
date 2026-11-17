package org.example.backend.rating.dtos;

/**
 * Represents the JSON body structure used for updating/creating duck ratings.
 *
 * @param userId Represents database id for a single user
 * @param duckId Represents the database id for a single duck
 * @param isPositive Whether the rating was positive
 */
public record RatingRequest(
    int userId,
    int duckId,
    boolean isPositive
) {}
