package org.example.backend.rating.dtos;

public record RatingRequest(
    int userId,
    int duckId,
    boolean isPositive
) {}
