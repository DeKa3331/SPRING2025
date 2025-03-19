package org.example;

import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    private final List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        System.out.println("Dodano pojazd: " + vehicle);
    }

    public void removeVehicle(int carId) {
        Vehicle vehicle = findVehicleById(carId);
        if (vehicle != null) {
            vehicles.remove(vehicle);
            System.out.println("Usunięto pojazd o ID " + carId);
        } else {
            System.out.println("Pojazd o ID " + carId + " nie istnieje.");
        }
    }

    public Vehicle findVehicleById(int carId) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getCarid() == carId)
                .findFirst()
                .orElse(null);
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }
}
