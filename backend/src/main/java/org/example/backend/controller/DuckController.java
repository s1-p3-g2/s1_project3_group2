package org.example.backend.controller;

import org.example.backend.dto.PostDuckRequest;
import org.example.backend.service.DuckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DuckController {
    private DuckService duckService;

    @PostMapping("api/v1/ducks")
        public ResponseEntity<Void> createDuck(@RequestBody PostDuckRequest pdr){
            duckService.createDuck(pdr);
            return ResponseEntity.noContent().build();
        }
}
