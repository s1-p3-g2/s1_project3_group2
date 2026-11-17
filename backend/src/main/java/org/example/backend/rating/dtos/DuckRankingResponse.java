package org.example.backend.rating.dtos;

/**
 * Represents the resulting JSON after calling ranking-related api requests.
 *
 * @param duckId Represents the database id for a single duck
 * @param score The # positive ratings - # negative ratings
 * @param position The rank/position relative to other ducks, scored by above value
 */
public record DuckRankingResponse(
    int duckId,
    int score,
    int position
) {

}
