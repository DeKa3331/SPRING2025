package org.example;


import java.util.List;
import java.util.Scanner;

public class CarRentalSystem {
    private Scanner scanner;
    private List<Vehicle> vehicles;

    public CarRentalSystem(Scanner scanner, List<Vehicle> vehicles) {
        this.scanner = scanner;
        this.vehicles = vehicles;
    }

    public void rentVehicle() {
        System.out.print("\nPodaj ID pojazdu do wypożyczenia: ");
        int carId = scanner.nextInt();
        scanner.nextLine();
        Vehicle vehicle = findVehicleById(carId);
        if (vehicle != null) {
            vehicle.rentVehicle(carId);
        } else {
            System.out.println("Pojazd o ID " + carId + " nie istnieje.");
        }
    }

    public Vehicle findVehicleById(int carid) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarid() == carid) {
                return vehicle;
            }
        }
        return null;
    }
}
