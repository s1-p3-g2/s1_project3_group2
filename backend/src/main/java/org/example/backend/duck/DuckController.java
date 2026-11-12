package org.example.backend.duck;

import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DuckController {
    private DuckService duckService;

    @PostMapping("api/v1/ducks")
        public ResponseEntity<PostDuckResponse> createDuck(@RequestBody PostDuckRequest pdr){
            PostDuckResponse createdDuck = duckService.createDuck(pdr);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdDuck);
            
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Bad request",
            "message", exception.getMessage()
        ));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EmptyResultDataAccessException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "Duck not found",
            "message", "No duck exists with the requested duck_id"
        ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseError(DataAccessException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Database error",
            "message", "The duck could not be processed"
        ));
    }
}
