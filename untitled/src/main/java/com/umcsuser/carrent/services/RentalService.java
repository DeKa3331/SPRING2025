package com.umcsuser.carrent.services;


import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Rental rentVehicle(User user, Vehicle vehicle) {
        if (!isVehicleAvailable(vehicle.getId())) {
            throw new IllegalStateException("Vehicle is already rented");
        }

        Rental rental = Rental.builder()
                .vehicleId(vehicle.getId())
                .userId(user.getId())
                .rentDateTime(LocalDateTime.now().format(formatter))
                .build();

        return rentalRepository.save(rental);
    }

    public Rental returnVehicle(Rental rental) {
        Rental existingRental = rentalRepository.findById(rental.getId())
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        existingRental.setReturnDateTime(LocalDateTime.now().format(formatter));
        return rentalRepository.save(existingRental);
    }

    public boolean isVehicleAvailable(String vehicleId) {
        List<Rental> rentals = rentalRepository.findByVehicleId(vehicleId);
        return rentals.stream()
                .noneMatch(r -> r.getReturnDateTime() == null);
    }

    public List<Rental> getUserRentals(String userId) {
        return rentalRepository.findByUserId(userId);
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }
}