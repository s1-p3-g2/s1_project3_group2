package main.java.org.example.backend.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.context.annotation.Primary;

// TODO: unfinished user class, here to prevent compile errors on ratings branch
@Entity
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
}
