package org.example.backend.comments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.backend.comments.model.CommentEntity;

public record CommentResponse(
        @JsonProperty("comment_id") Integer commentId,
        @JsonProperty("user_id") Integer userId,
        @JsonProperty("duck_id") Integer duckId,
        String comment) {

    public static CommentResponse from(CommentEntity entity) {
        return new CommentResponse(
                entity.getId(), entity.getUserId(), entity.getDuckId(), entity.getComment());
    }
}
