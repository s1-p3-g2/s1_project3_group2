package org.example.backend.duck;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Azrael
 * @version 0.1.0
 * Description: how you get ducks for the leaderboard
 * @since 11/18/2026
 */
@Repository
public interface DuckRepository extends JpaRepository<Duck, Integer> {

    //All ducks in order
    List<Duck> findAllByOrderByRatingScoreDesc();
}