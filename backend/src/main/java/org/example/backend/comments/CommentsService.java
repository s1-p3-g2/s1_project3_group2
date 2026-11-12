package org.example.backend.comments;

import java.util.List;
import org.example.backend.comments.dto.CommentResponse;
import org.example.backend.comments.dto.CommentTextRequest;
import org.example.backend.comments.dto.CreateCommentRequest;
import org.example.backend.comments.model.CommentEntity;
import org.example.backend.comments.repository.CommentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentsService {

    private static final int MAX_COMMENT_LENGTH = 500;

    private final CommentsRepository commentsRepository;

    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getDuck(Integer duckId) {
        validateId(duckId, "duck_id");
        return commentsRepository.findAllByDuckIdOrderByIdAsc(duckId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponse getById(Integer commentId) {
        return CommentResponse.from(findById(commentId));
    }

    public CommentResponse create(Integer duckId, CreateCommentRequest request) {
        validateId(duckId, "duck_id");
        if (request == null || request.userId() == null || request.userId() <= 0) {
            throw new InvalidCommentException("user_id must be a positive integer");
        }
        String text = validateText(request.comment());
        return CommentResponse.from(commentsRepository.save(
                new CommentEntity(request.userId(), duckId, text)));
    }

    public CommentResponse replace(Integer commentId, CommentTextRequest request) {
        CommentEntity entity = findById(commentId);
        entity.setComment(validateText(request == null ? null : request.comment()));
        return CommentResponse.from(commentsRepository.save(entity));
    }

    public CommentResponse patch(Integer commentId, CommentTextRequest request) {
        // The only editable field is the comment text, so PATCH and PUT have the same body.
        return replace(commentId, request);
    }

    public void delete(Integer commentId) {
        CommentEntity entity = findById(commentId);
        commentsRepository.delete(entity);
    }

    private CommentEntity findById(Integer commentId) {
        validateId(commentId, "comment_id");
        return commentsRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    private void validateId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new InvalidCommentException(fieldName + " must be a positive integer");
        }
    }

    private String validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new InvalidCommentException("comment may not be blank");
        }
        if (text.length() > MAX_COMMENT_LENGTH) {
            throw new InvalidCommentException("comment may be at most 500 characters");
        }
        return text.trim();
    }
}
