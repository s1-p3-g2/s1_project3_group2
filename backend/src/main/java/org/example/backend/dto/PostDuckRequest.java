package org.example.backend.dto;

public record PostDuckRequest(
    int duck_id,
    String name,
    String image_link,
    String description,
    int creator_id
) {}
