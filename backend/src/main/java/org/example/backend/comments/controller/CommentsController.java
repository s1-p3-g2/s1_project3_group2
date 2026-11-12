package org.example.backend.comments.controller;

import java.net.URI;
import java.util.List;
import org.example.backend.comments.dto.CommentResponse;
import org.example.backend.comments.dto.CommentTextRequest;
import org.example.backend.comments.dto.CreateCommentRequest;
import org.example.backend.comments.service.CommentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommentsController {

    private final CommentsService commentsService;

    public CommentsController(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @GetMapping("/ducks/{duckId}/comments")
    public List<CommentResponse> getDuck(@PathVariable Integer duckId) {
        return commentsService.getDuck(duckId);
    }

    @PostMapping("/ducks/{duckId}/comments")
    public ResponseEntity<CommentResponse> create(
            @PathVariable Integer duckId,
            @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentsService.create(duckId, request);
        return ResponseEntity.created(URI.create("/api/comments/" + response.commentId()))
                .body(response);
    }

    @GetMapping("/comments/{commentId}")
    public CommentResponse getById(@PathVariable Integer commentId) {
        return commentsService.getById(commentId);
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponse replace(
            @PathVariable Integer commentId,
            @RequestBody CommentTextRequest request) {
        return commentsService.replace(commentId, request);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentResponse patch(
            @PathVariable Integer commentId,
            @RequestBody CommentTextRequest request) {
        return commentsService.patch(commentId, request);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Integer commentId) {
        commentsService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
