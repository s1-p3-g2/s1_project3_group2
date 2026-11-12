package org.example.backend.duck;

public record PostDuckRequest(
    String name,
    String image_link,
    String description,
    int creator_id
) {}
