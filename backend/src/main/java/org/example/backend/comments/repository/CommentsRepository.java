package org.example.backend.comments.repository;

import java.util.List;
import org.example.backend.comments.model.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findAllByDuckIdOrderByIdAsc(Integer duckId);
}
