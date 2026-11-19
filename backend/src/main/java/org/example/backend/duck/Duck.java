
package org.example.backend.duck;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ducks")
public class Duck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "duck_id", nullable = false)
    private int duckId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "description")
    private String description;

    @Column(name = "creator_id", nullable = false)
    private int creatorId;

    @Column(name = "rating_score")
    private int ratingScore;

    // Constructors
    public Duck() {
    }

    public Duck(int duckId) {
        this.duckId = duckId;
    }

    public Duck(String name, String imageLink, String description, int creatorId, int ratingScore) {
        this.name = name;
        this.imageLink = imageLink;
        this.description = description;
        this.creatorId = creatorId;
        this.ratingScore = ratingScore;
    }

    // Getters
    public int getDuckId() {
        return duckId;
    }

    public String getName() {
        return name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getDescription() {
        return description;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    // Setters
    public void setDuckId(int duckId) {
        this.duckId = duckId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public void setRatingScore(int rating_score) {
        this.ratingScore = rating_score;
    }
}

