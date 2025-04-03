package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;
import java.util.Scanner;

public class UserService {
    private VehicleRepository vehicleRepo;
    private RentalService rentalService;

    public UserService(VehicleRepository vehicleRepo, RentalService rentalService) {
        this.vehicleRepo = vehicleRepo;
        this.rentalService = rentalService;
    }

    public void rentVehicle(User currentUser, Scanner scanner) {
        System.out.println("Wybierz ID pojazdu do wypożyczenia:");
        String vehicleId = scanner.nextLine();

        Vehicle vehicle = vehicleRepo.findById(vehicleId).orElse(null);
        if (vehicle != null && rentalService.isVehicleAvailable(vehicleId)) {
            rentalService.rentVehicle(currentUser, vehicle);
            System.out.println("Wypożyczono pojazd: " + vehicle);
        } else {
            System.out.println("Pojazd jest już wypożyczony lub nie istnieje.");
        }
    }

    public void returnVehicle(User currentUser) {
        List<Rental> userRentals = rentalService.getUserRentals(currentUser.getId());
        userRentals.stream()
                .filter(r -> r.getReturnDateTime() == null)
                .forEach(rental -> {
                    Rental updatedRental = rentalService.returnVehicle(rental);
                    System.out.println("Zwrócono pojazd: " + updatedRental);
                });
    }

    public void listAvailableVehicles() {
        System.out.println("Dostępne pojazdy:");
        vehicleRepo.findAll().stream()
                .filter(v -> rentalService.isVehicleAvailable(v.getId()))
                .forEach(System.out::println);
    }
}