package com.example.model;

import jakarta.persistence.*;

@Entity
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    
    @Column(length = 2000) // JSON String for Map Coordinates
    private String mapCoordinates; 

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMapCoordinates() { return mapCoordinates; }
    public void setMapCoordinates(String mapCoordinates) { this.mapCoordinates = mapCoordinates; }
 // ... existing code ...
    @Transient private Double averageRating = 0.0;
    @Transient private Integer reviewCount = 0;
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}