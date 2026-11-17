package org.example.backend.comments.service;

import java.io.Serial;

public class CommentNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CommentNotFoundException(Integer commentId) {
        super("Comment " + commentId + " was not found");
    }
}
