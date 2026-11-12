package org.example.backend.rating;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RatingIntegrationTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine")
      .withDatabaseName("ratemyduck")
      .withUsername("test")
      .withPassword("test");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RatingRepository ratingRepository;

  @BeforeEach
  void resetRatings() {
    ratingRepository.deleteAll();
  }

  @Test
  void httpRatingCreateTest() throws Exception {
    mockMvc.perform(post("/api/v1/ratings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"userId\":101,\"duckId\":202,\"isPositive\":false}"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId").value(101))
        .andExpect(jsonPath("$.duckId").value(202))
        .andExpect(jsonPath("$.isPositive").value(false));
  }

  @Test
  void httpRatingUpdateTest() throws Exception {
    ratingRepository.save(new Rating(101, 202, false));

    mockMvc.perform(put("/api/v1/ratings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"userId\":101,\"duckId\":202,\"isPositive\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isPositive").value(true));
  }

  @Test
  void httpRatingGetTest() throws Exception {
    ratingRepository.save(new Rating(101, 202, false));

    mockMvc.perform(get("/api/v1/ratings/ducks/202/users/101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isPositive").value(false));

    mockMvc.perform(get("/api/v1/ratings/users/101"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].duckId").value(202));

    mockMvc.perform(get("/api/v1/ratings/ducks/202"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void httpRatingDeleteTest() throws Exception {
    ratingRepository.save(new Rating(101, 202, false));
    mockMvc.perform(delete("/api/v1/ratings/ducks/202/users/101"))
        .andExpect(status().isOk());

    assertTrue(ratingRepository.findByRatingId_DuckIdAndRatingId_UserId(202, 101).isEmpty());
  }

  @Test
  void ratingsCollectionReturnsSpringPageShape() throws Exception {
    ratingRepository.save(new Rating(301, 401, true));

    mockMvc.perform(get("/api/v1/ratings"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].userId").value(301));
  }
}
