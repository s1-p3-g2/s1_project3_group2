package org.example.backend.comments.service;

import java.io.Serial;

public class InvalidCommentException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCommentException(String message) {
        super(message);
    }
}
