package org.example.backend.comments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCommentRequest(
        @JsonProperty("user_id") Integer userId,
        String comment) {
}
