package java.org.example.backend.rating;


import main.java.org.example.backend.rating.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.bean.override.convention.TestBean;

import java.util.Optional;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * @author Paul Conner
 * @since
 *
 **/

@DataJpaTest
public class RatingRepositoryTest {
    @Autowired
    RatingRepository ratingRepository;

    @Test
    void saveRatingTest(){
        Rating rating = new Rating(1, 1, false);
        Rating saveResult = ratingRepository.save(rating);
        assertEquals("Saved Rating Comparison", rating, saveResult);
    }

    @Test
    void findByIdRatingTest(){
        Rating rating = new Rating(1, 1, false);
        ratingRepository.save(rating);

        Optional<Rating> result = ratingRepository.findByDuckIdAndUserId(
                rating.getRatingId().getDuckId(),
                rating.getRatingId().getDuckId()
        );
        assertTrue("Found Rating exists", result.isPresent());
        assertEquals("Found Rating Comparison", rating, result);
    }
}
