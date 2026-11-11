package org.example.backend.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.context.annotation.Primary;

// TODO: unfinished user class, here to prevent compile errors on ratings branch
@Entity
@Table(name="users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    public User(int userId) {
        this.userId = userId;
    }

  public User() {

  }

  public int getUserId() {
        return userId;
    }
}
