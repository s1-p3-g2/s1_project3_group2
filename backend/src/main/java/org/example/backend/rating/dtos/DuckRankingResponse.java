package org.example.backend.rating.dtos;

public record DuckRankingResponse(
    int duckId,
    int score,
    int position
) {

}
