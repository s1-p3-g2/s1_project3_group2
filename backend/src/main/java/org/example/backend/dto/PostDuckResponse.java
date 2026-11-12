package org.example.backend.dto;

public record PostDuckResponse(
    String name,
    String image_link,
    String description,
    int creator_id,
    int duck_id
) 
{}