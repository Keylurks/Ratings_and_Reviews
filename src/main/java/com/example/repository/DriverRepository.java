package com.example.repository;


import com.example.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Basic CRUD operations (Save, Delete, Find) are built-in automatically.
}