package org.example.backend.duck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class DuckControllerTest {
    private DuckService duckService;
    private DuckController controller;

    @BeforeEach
    void setUp() throws Exception {
        duckService = mock(DuckService.class);
        controller = new DuckController();

        var field = DuckController.class.getDeclaredField("duckService");
        field.setAccessible(true);
        field.set(controller, duckService);
    }

    @Test
    void createDuck_returnsCreatedResponse() {
        PostDuckRequest request = new PostDuckRequest("Mallard", "duck.png", "A mallard", 7);
        PostDuckResponse expected = new PostDuckResponse("Mallard", "A mallard", "duck.png", 7, 42);
        when(duckService.createDuck(request)).thenReturn(expected);

        ResponseEntity<PostDuckResponse> response = controller.createDuck(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void handleBadRequest_returns400AndMessage() {
        ResponseEntity<Map<String, String>> response = controller.handleBadRequest(
            new IllegalArgumentException("Fields must not be blank")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of(
            "error", "Bad request",
            "message", "Fields must not be blank"
        ), response.getBody());
    }

    @Test
    void handleNotFound_returns404AndFixedMessage() {
        ResponseEntity<Map<String, String>> response = controller.handleNotFound(
            new EmptyResultDataAccessException(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Map.of(
            "error", "Duck not found",
            "message", "No duck exists with the requested duck_id"
        ), response.getBody());
    }

    @Test
    void handleDatabaseError_returns500AndFixedMessage() {
        DataAccessException exception = new DataAccessException("database unavailable") {};

        ResponseEntity<Map<String, String>> response = controller.handleDatabaseError(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(Map.of(
            "error", "Database error",
            "message", "The duck could not be processed"
        ), response.getBody());
    }
}
