package com.example.komyut;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final DriverRepository driverRepo;
    private final RouteRepository routeRepo;
    private final VehicleRepository vehicleRepo;

    public DataSeeder(DriverRepository driverRepo, RouteRepository routeRepo, VehicleRepository vehicleRepo) {
        this.driverRepo = driverRepo;
        this.routeRepo = routeRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public void run(String... args) {
        // 1. VEHICLES (Added Taxi, Total 5 Types)
        if (vehicleRepo.count() == 0) {
            saveVehicle("Traditional Jeepney", "TVJ-143");
            saveVehicle("Modern Jeepney (E-Jeep)", "MJE-001");
            saveVehicle("Aircon Bus", "BUS-888");
            saveVehicle("Tricycle", "TRI-505");
            saveVehicle("Taxi (Sedan)", "TAXI-999"); // Added Taxi
        }

        // 2. DRIVERS (Total 5 Filipino Drivers)
        if (driverRepo.count() == 0) {
            saveDriver("Mang Benjie", "D01-23-45678");
            saveDriver("Kuya Carding", "D02-44-12345");
            saveDriver("Manong Jhun", "D03-11-99999");
            saveDriver("Ate Grace", "D04-55-11111");     // New
            saveDriver("Kuya Romy", "D05-88-77777");     // New
        }

        // 3. ROUTES (Total 5 Real Manila Routes)
        if (routeRepo.count() == 0) {
            // Route 1: Quiapo -> Lawton (Passing Quezon Bridge)
            String quiapoCoords = "[[14.6042, 120.9822], [14.6010, 120.9835], [14.5980, 120.9830], [14.5960, 120.9800]]";
            saveRoute("Quiapo - Lawton", "Passing through Quezon Bridge, Post Office", quiapoCoords);
            
            // Route 2: Cubao -> Divisoria (Magsaysay Blvd)
            String cubaoCoords = "[[14.6178, 121.0572], [14.6030, 121.0130], [14.6020, 120.9730]]";
            saveRoute("Cubao - Divisoria", "Via Aurora Blvd, Magsaysay Blvd", cubaoCoords);

            // Route 3: Monumento -> Baclaran (EDSA Carousel Style)
            String edsaCoords = "[[14.6576, 120.9839], [14.6019, 121.0355], [14.5323, 120.9926]]";
            saveRoute("Monumento - Baclaran", "Via EDSA, passing Trinoma, Megamall, MOA", edsaCoords);

            // Route 4: Fairview -> Ayala (Commonwealth Ave)
            String fairviewCoords = "[[14.7176, 121.0668], [14.6515, 121.0493], [14.5547, 121.0244]]";
            saveRoute("Fairview - Ayala", "Via Commonwealth Ave, Philcoa, Quezon Ave", fairviewCoords);

            // Route 5: Pasig -> Quiapo (Crossing)
            String pasigCoords = "[[14.5583, 121.0850], [14.5866, 121.0567], [14.5996, 120.9842]]";
            saveRoute("Pasig - Quiapo", "Via Shaw Blvd, Crossing, Sta. Mesa", pasigCoords);
        }
    }

    private void saveVehicle(String type, String plate) {
        Vehicle v = new Vehicle(); v.setType(type); v.setPlateNumber(plate); vehicleRepo.save(v);
    }
    private void saveDriver(String name, String license) {
        Driver d = new Driver(); d.setName(name); d.setLicenseNumber(license); driverRepo.save(d);
    }
    private void saveRoute(String name, String desc, String coords) {
        Route r = new Route(); r.setName(name); r.setDescription(desc); r.setMapCoordinates(coords); routeRepo.save(r);
    }
}