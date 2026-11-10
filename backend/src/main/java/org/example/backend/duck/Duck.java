package main.java.org.example.backend.duck;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// TODO: unfinished duck class, here to prevent compile errors on ratings branch
@Entity
public class Duck {
    @Id
    @Column(name = "duck_id", nullable = false)
    private int duckId;
}
