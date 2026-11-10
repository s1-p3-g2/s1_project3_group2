package java.org.example.backend.rating;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RatingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRatingTest() throws Exception {
        // Arrange
        int userId = 1;
        int duckId = 1;

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/ratings/ducks/{duckId}/users/{userId}", duckId, userId));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.duckId").value(duckId))
                .andExpect(jsonPath("$.isPositive").value(false))
        ;
    }

    // TODO: finish getRatingsTest, address pages
    @Test
    public void getRatingsTest() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/ratings"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getRatingsByUserIdTest() throws Exception {
        // Arrange
        int userId = 1;

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/ratings/users/{userId}", userId));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getRatingsByDuckIdTest() throws Exception {
        // Arrange
        int duckId = 1;

        // Act
        ResultActions result = mockMvc.perform(get("/api/v1/ratings/ducks/{duckId}", duckId));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void createRatingTest() throws Exception {
        // Arrange
        String ratingJson = "{\"userId\":\"1\",\"duckId\":\"1\",\"isPositive\":false}";

        // Act
        ResultActions result = mockMvc.perform(post("/api/v1/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingJson));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/ratings/ducks/1/users/1"));

    }

    @Test
    public void updateRatingTest() throws Exception {
        // Arrange
        int userId = 1;
        int duckId = 1;
        String updatedRatingJson = "{\"userId\":\"1\",\"duckId\":\"1\",\"isPositive\":true}";

        // Act
        ResultActions result = mockMvc.perform(put("/api/v1/ratings/ducks/{duckId}/users/{userId}", duckId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedRatingJson));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.duckId").value(duckId))
                .andExpect(jsonPath("$.isPositive").value(true));
    }

    @Test
    public void deleteRatingTest() throws Exception {
        // Arrange
        int userId = 1;
        int duckId = 1;

        // Act
        ResultActions result = mockMvc.perform(delete("/api/v1/ratings/ducks/{duckId}/users/{userId}", duckId, userId));

        // Assert
        result.andExpect(status().isOk());
    }
}