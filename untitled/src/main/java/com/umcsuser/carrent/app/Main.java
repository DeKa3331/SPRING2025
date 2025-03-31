package com.umcsuser.carrent.app;


import com.umcsuser.carrent.models.*;
import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Inicjalizacja repozytoriów
        UserRepository userRepository = new UserJsonRepository();
        VehicleRepository vehicleRepository = new VehicleJsonRepository();
        RentalRepository rentalRepository = new RentalJsonRepository();

        // Inicjalizacja serwisów
        AuthService authService = new AuthService(userRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository);
        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository);

        // Przykładowe użycie
        try {
            // Rejestracja admina
            //User admin = authService.register("admin", "admin123", "ADMIN");

            // Rejestracja użytkownika
            //User user = authService.register("user1", "user123", "USER");

            // Dodanie pojazdu przez admina
            Vehicle bus = Vehicle.builder()
                    .category("Bus")
                    .brand("Volkswagen")
                    .model("T2")
                    .year(1985)
                    .plate("LU123")
                    .build();
            bus.addAttribute("seats", 20);
            vehicleService.addVehicle(bus);

            // Wypożyczenie pojazdu przez użytkownika
            Optional<User> loggedInUser = authService.login("user1", "user123");
            if (loggedInUser.isPresent()) {
                List<Vehicle> availableVehicles = vehicleService.getAllVehicles().stream()
                        .filter(v -> rentalService.isVehicleAvailable(v.getId()))
                        .collect(Collectors.toList());

                if (!availableVehicles.isEmpty()) {
                    Rental rental = rentalService.rentVehicle(loggedInUser.get(), availableVehicles.get(0));
                    System.out.println("Vehicle rented: " + rental);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
