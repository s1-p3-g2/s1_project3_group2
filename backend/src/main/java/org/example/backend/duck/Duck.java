package org.example.backend.duck;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// TODO: unfinished duck class, here to prevent compile errors on ratings branch
@Entity
@Table(name="ducks")
public class Duck {
    @Id
    @Column(name = "duck_id", nullable = false)
    private int duckId;

    public Duck(int duckId) {
        this.duckId = duckId;
    }

    public int getDuckId() {
        return duckId;
    }
}
