package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository implements IVehicleRepository {
    private final List<Vehicle> vehicles = new ArrayList<>();

    @Override
    public void rentVehicle(int carid) {
        Vehicle vehicle = findVehicleById(carid);
        if (vehicle != null) {
            if (!vehicle.isRented()) {
                vehicle.setRented(true);
                System.out.println("Pojazd o ID " + carid + " został wynajęty.");
                save();
            } else {
                System.out.println("Pojazd o ID " + carid + " jest już wynajęty.");
            }
        } else {
            System.out.println("Pojazd o ID " + carid + " nie istnieje w bazie.");
        }
    }

    @Override
    public void returnVehicle(int carid) {
        Vehicle vehicle = findVehicleById(carid);
        if (vehicle != null) {
            if (vehicle.isRented()) {
                vehicle.setRented(false);
                System.out.println("Pojazd o ID " + carid + " został zwrócony.");
                save();
            } else {
                System.out.println("Pojazd o ID " + carid + " nie był wynajęty.");
            }
        } else {
            System.out.println("Pojazd o ID " + carid + " nie istnieje w bazie.");
        }
    }

    @Override
    public void save() {
        saveToCsv(Paths.get("Vehicles.csv"));
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        System.out.println("Dodano pojazd: " + vehicle);
    }

    @Override
    public void removeVehicle(int carId) {
        Vehicle vehicle = findVehicleById(carId);
        if (vehicle != null) {
            vehicles.remove(vehicle);
            System.out.println("Usunięto pojazd o ID " + carId);
        } else {
            System.out.println("Pojazd o ID " + carId + " nie istnieje.");
        }
    }

    @Override
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    // Dodatkowa metoda do wyszukiwania pojazdu po ID
    public Vehicle findVehicleById(int carId) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getCarid() == carId)
                .findFirst()
                .orElse(null);
    }

    // Zapis do pliku CSV
    public void saveToCsv(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("carid;brand;model;year;price;rented;category\n");
            for (Vehicle vehicle : vehicles) {
                writer.write(vehicle.toCSV() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisywania do pliku CSV: " + e.getMessage());
        }
    }
}
