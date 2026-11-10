package org.example.backend.controller;

import org.example.backend.dto.PostDuckRequest;
import org.example.backend.service.DuckService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DuckController {
    private DuckService duckService;

    @PostMapping("api/v1/ducks")
        public void createDuck(PostDuckRequest pdr){
            duckService.createDuck(pdr);
        }
}
