package com.example.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.model.*; // Import all models
import com.example.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ReviewRepository reviewRepo;
    @Autowired private DriverRepository driverRepo;
    @Autowired private RouteRepository routeRepo;
    @Autowired private VehicleRepository vehicleRepo;

    @GetMapping("")
    public String adminDashboard(Model model) {
        
        // 1. FETCH AND CALCULATE DRIVER STATS
        List<Driver> drivers = driverRepo.findAll();
        for (Driver d : drivers) {
            Double avg = reviewRepo.getAvgRatingByDriver(d.getId());
            Long count = reviewRepo.countByDriverIdAndIsHiddenFalse(d.getId());
            d.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0); // Round to 1 decimal
            d.setReviewCount(count.intValue());
        }
        model.addAttribute("drivers", drivers);

        // 2. FETCH AND CALCULATE ROUTE STATS
        List<Route> routes = routeRepo.findAll();
        for (Route r : routes) {
            Double avg = reviewRepo.getAvgRatingByRoute(r.getId());
            Long count = reviewRepo.countByRouteIdAndIsHiddenFalse(r.getId());
            r.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
            r.setReviewCount(count.intValue());
        }
        model.addAttribute("routes", routes);

        // 3. FETCH AND CALCULATE VEHICLE STATS
        List<Vehicle> vehicles = vehicleRepo.findAll();
        for (Vehicle v : vehicles) {
            Double avg = reviewRepo.getAvgRatingByVehicle(v.getId());
            Long count = reviewRepo.countByVehicleIdAndIsHiddenFalse(v.getId());
            v.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
            v.setReviewCount(count.intValue());
        }
        model.addAttribute("vehicles", vehicles);

        // 4. GLOBAL ANALYTICS (Existing code)
        model.addAttribute("reviews", reviewRepo.findAll());
        Double overallAvg = reviewRepo.getAverageRating();
        model.addAttribute("overallRating", overallAvg != null ? String.format("%.1f", overallAvg) : "0.0");
        model.addAttribute("totalReviews", reviewRepo.count());
        model.addAttribute("hiddenReviews", reviewRepo.countByIsHiddenTrue());

        Double dAvg = reviewRepo.getDriverAverage();
        Double rAvg = reviewRepo.getRouteAverage();
        Double vAvg = reviewRepo.getVehicleAverage();
        model.addAttribute("driverAvg", dAvg != null ? String.format("%.1f", dAvg) : "0.0");
        model.addAttribute("routeAvg", rAvg != null ? String.format("%.1f", rAvg) : "0.0");
        model.addAttribute("vehicleAvg", vAvg != null ? String.format("%.1f", vAvg) : "0.0");

        // 5. CHART DATA
        List<Object[]> dist = reviewRepo.getRatingDistribution();
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : dist) map.put((Integer) row[0], (Long) row[1]);
        long[] starCounts = new long[5];
        for(int i=1; i<=5; i++) starCounts[i-1] = map.getOrDefault(i, 0L);
        model.addAttribute("starCounts", starCounts);

        return "admin";
    }

    // ... (Keep existing Hide/Show/Delete/QR methods exactly as they were) ...
    @PostMapping("/hide/{id}")
    public String hideReview(@PathVariable Long id) {
        reviewRepo.findById(id).ifPresent(review -> {
            review.setHidden(true);
            reviewRepo.save(review);
        });
        return "redirect:/admin";
    }

    @PostMapping("/show/{id}")
    public String showReview(@PathVariable Long id) {
        reviewRepo.findById(id).ifPresent(review -> {
            review.setHidden(false);
            reviewRepo.save(review);
        });
        return "redirect:/admin";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewRepo.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/qr")
    public void generateQRCode(@RequestParam String type, @RequestParam Long id, HttpServletResponse response) throws Exception {
        String baseUrl = "https://ratings-and-reviews-3m04.onrender.com/rate?"; 
        String data = "";
        if(type.equals("driver")) data = baseUrl + "driverId=" + id;
        if(type.equals("route")) data = baseUrl + "routeId=" + id;
        if(type.equals("vehicle")) data = baseUrl + "vehicleId=" + id;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        response.setContentType("image/png");
        OutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.flush();
    }

}

