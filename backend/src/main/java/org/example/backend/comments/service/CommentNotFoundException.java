package org.example.backend.comments.service;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Integer commentId) {
        super("Comment " + commentId + " was not found");
    }
}
