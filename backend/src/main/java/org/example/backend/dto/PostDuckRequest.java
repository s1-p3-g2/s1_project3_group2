package org.example.backend.dto;

public record PostDuckRequest(
    String name,
    String image_link,
    String description,
    int creator_id
) {}
