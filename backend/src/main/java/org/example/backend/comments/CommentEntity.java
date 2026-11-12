package org.example.backend.comments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "duck_id", nullable = false)
    private Integer duckId;

    @Column(name = "comment_text", nullable = false, length = 500)
    private String comment;

    protected CommentEntity() {
    }

    public CommentEntity(Integer userId, Integer duckId, String comment) {
        this.userId = userId;
        this.duckId = duckId;
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer Id() {
        return duckId;
    }

    public void setDuckId(Integer duckId) {
        this.duckId = duckId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
