package org.example.backend.duck;

public record PostDuckResponse(
    String name,
    String image_link,
    String description,
    int creator_id,
    int duck_id
) 
{}