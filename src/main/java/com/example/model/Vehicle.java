package com.example.model;

import jakarta.persistence.*;

@Entity
public class Vehicle {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type;        // e.g., Jeepney, Bus
    private String plateNumber; // e.g., ABC-123

    // --- GETTERS ---
    public Long getId() { 
        return id; 
    }
    
    public String getType() { 
        return type; 
    }
    
    public String getPlateNumber() { 
        return plateNumber; 
    }

    // --- SETTERS (These were likely missing) ---
    public void setId(Long id) { 
        this.id = id; 
    }

    public void setType(String type) { 
        this.type = type; 
    }

    public void setPlateNumber(String plateNumber) { 
        this.plateNumber = plateNumber; 
    }
 // ... existing code ...
    @Transient private Double averageRating = 0.0;
    @Transient private Integer reviewCount = 0;

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}