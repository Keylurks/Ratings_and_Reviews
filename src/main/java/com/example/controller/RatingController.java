package com.example.controller;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RatingController {

    @Autowired private DriverRepository driverRepo;
    @Autowired private RouteRepository routeRepo;
    @Autowired private VehicleRepository vehicleRepo;
    @Autowired private ReviewRepository reviewRepo;

    // Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        List<Review> allReviews = reviewRepo.findByIsHiddenFalse();
        
        List<Review> driverReviews = new ArrayList<>();
        List<Review> routeReviews = new ArrayList<>();
        List<Review> vehicleReviews = new ArrayList<>();

        for (Review r : allReviews) {
            if (r.getDriver() != null) driverReviews.add(r);
            if (r.getRoute() != null) routeReviews.add(r);
            if (r.getVehicle() != null) vehicleReviews.add(r);
        }

        model.addAttribute("driverReviews", driverReviews);
        model.addAttribute("routeReviews", routeReviews);
        model.addAttribute("vehicleReviews", vehicleReviews);
        
        return "dashboard";
    }

    // Home Redirect
    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin" : "redirect:/dashboard";
        }
        return "redirect:/rate";
    }

    // --- RATE FORM (Handles QR Codes + Manual Select + HISTORY DISPLAY) ---
    @GetMapping("/rate")
    public String showRateForm(
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) Long vehicleId,
            Model model) {
        
        // 1. If QR Code used (ID exists), load that specific object
        if (driverId != null) model.addAttribute("selectedDriver", driverRepo.findById(driverId).orElse(null));
        if (routeId != null) model.addAttribute("selectedRoute", routeRepo.findById(routeId).orElse(null));
        if (vehicleId != null) model.addAttribute("selectedVehicle", vehicleRepo.findById(vehicleId).orElse(null));

        // 2. Load ALL lists for the dropdowns
        model.addAttribute("allDrivers", driverRepo.findAll());
        model.addAttribute("allRoutes", routeRepo.findAll());
        model.addAttribute("allVehicles", vehicleRepo.findAll());
        
        // 3. FETCH HISTORY REVIEWS (Smart Logic)
        List<Review> historyReviews = new ArrayList<>();
        String historyTitle = "Recent Reviews";

        if (driverId != null) {
            historyReviews.addAll(reviewRepo.findByDriverIdAndIsHiddenFalse(driverId));
            historyTitle = "Reviews for this Driver";
        } 
        else if (routeId != null) {
            historyReviews.addAll(reviewRepo.findByRouteIdAndIsHiddenFalse(routeId));
            historyTitle = "Reviews for this Route";
        } 
        else if (vehicleId != null) {
            historyReviews.addAll(reviewRepo.findByVehicleIdAndIsHiddenFalse(vehicleId));
            historyTitle = "Reviews for this Vehicle";
        } 
        else {
            // Generic Page: Show everything (or limit to top 10 if you prefer later)
            historyReviews = reviewRepo.findByIsHiddenFalse();
            historyTitle = "Latest Community Reviews";
        }

        model.addAttribute("historyReviews", historyReviews);
        model.addAttribute("historyTitle", historyTitle);

        return "rate";
    }

    // --- SUBMIT RATING ---
    @PostMapping("/rate")
    public String submitRating(
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Integer driverRating,
            @RequestParam(required = false) String driverComment,

            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) Integer routeRating,
            @RequestParam(required = false) String routeComment,

            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Integer vehicleRating,
            @RequestParam(required = false) String vehicleComment,

            Authentication auth) {

        String reviewerName = (auth != null && auth.isAuthenticated()) ? auth.getName() : "Anonymous (QR Scan)";

        // 1. Save Driver Review
        if (driverId != null && driverRating != null) {
            Review r = new Review();
            r.setRating(driverRating);
            r.setComment(driverComment);
            r.setReviewerName(reviewerName);
            r.setDriver(driverRepo.findById(driverId).orElse(null));
            reviewRepo.save(r);
        }

        // 2. Save Route Review
        if (routeId != null && routeRating != null) {
            Review r = new Review();
            r.setRating(routeRating);
            r.setComment(routeComment);
            r.setReviewerName(reviewerName);
            r.setRoute(routeRepo.findById(routeId).orElse(null));
            reviewRepo.save(r);
        }

        // 3. Save Vehicle Review
        if (vehicleId != null && vehicleRating != null) {
            Review r = new Review();
            r.setRating(vehicleRating);
            r.setComment(vehicleComment);
            r.setReviewerName(reviewerName);
            r.setVehicle(vehicleRepo.findById(vehicleId).orElse(null));
            reviewRepo.save(r);
        }

        return "redirect:/?success";
    }
}