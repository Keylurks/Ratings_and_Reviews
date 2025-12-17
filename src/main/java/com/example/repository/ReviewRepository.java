package com.example.repository;

import com.example.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // --- BASIC FETCHING ---
    List<Review> findByIsHiddenFalse();
    List<Review> findByDriverIdAndIsHiddenFalse(Long driverId);
    List<Review> findByRouteIdAndIsHiddenFalse(Long routeId);
    List<Review> findByVehicleIdAndIsHiddenFalse(Long vehicleId);

    // --- GLOBAL ANALYTICS ---
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.isHidden = false")
    Double getAverageRating();

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.isHidden = false GROUP BY r.rating")
    List<Object[]> getRatingDistribution();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.driver IS NOT NULL AND r.isHidden = false")
    Double getDriverAverage();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.route IS NOT NULL AND r.isHidden = false")
    Double getRouteAverage();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicle IS NOT NULL AND r.isHidden = false")
    Double getVehicleAverage();
    
    Long countByIsHiddenTrue();

    // --- NEW: INDIVIDUAL ANALYTICS ---
    
    // Driver Stats
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.driver.id = :id AND r.isHidden = false")
    Double getAvgRatingByDriver(Long id);
    Long countByDriverIdAndIsHiddenFalse(Long id);

    // Route Stats
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.route.id = :id AND r.isHidden = false")
    Double getAvgRatingByRoute(Long id);
    Long countByRouteIdAndIsHiddenFalse(Long id);

    // Vehicle Stats
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicle.id = :id AND r.isHidden = false")
    Double getAvgRatingByVehicle(Long id);
    Long countByVehicleIdAndIsHiddenFalse(Long id);
}