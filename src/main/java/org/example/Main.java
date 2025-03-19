package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("Vehicles.csv");
        List<Vehicle> vehicleList = Vehicle.fromCsv(path);
        System.out.println("Liczba pojazdów: " + vehicleList.size());

        if (vehicleList.isEmpty()) {
            System.out.println("Brak pojazdów w bazie!");
        }

        System.out.println("Dostępne pojazdy:");
        for (Vehicle vehicle : vehicleList) {
            vehicle.displayInfo();
        }

        Vehicle vehicle1 = findVehicleById(vehicleList, 1);
        if (vehicle1 != null) {
            vehicle1.rentVehicle(1);
            vehicle1.save();
        }

        System.out.println("\nPo wynajęciu pojazdu:");
        for (Vehicle vehicle : vehicleList) {
            vehicle.displayInfo();
        }

        if (vehicle1 != null) {
            vehicle1.returnVehicle(vehicle1.getCarid());
            vehicle1.save();
        }

        System.out.println("\nPo zwróceniu pojazdu:");
        for (Vehicle vehicle : vehicleList) {
            vehicle.displayInfo();
        }

        Vehicle car1 = new Car("Toyota", "Corolla", 2020, 100, 1, false);
        Vehicle car2 = new Car("Toyota", "Corolla", 2020, 100, 2, false);
        Vehicle car3 = new Car("Honda", "Civic", 2019, 90, 3, false);
        Vehicle car4 = new Motorcycle("Honda", "CBR500", 2021, 120, 4, false, "Sport");

        System.out.println("=== TEST: RÓŻNE OBIEKTY ===");
        System.out.println(car1.equals(car2));
        System.out.println(car1.equals(car3));
        System.out.println(car1.equals(car4));
        System.out.println("hashCode car1 == car2: " + (car1.hashCode() == car2.hashCode()));
        System.out.println("hashCode car1 == car3: " + (car1.hashCode() == car3.hashCode()));

        List<Vehicle> vehicleList2 = new ArrayList<>(List.of(car1, car2, car3, car4));
        car1.setVehicles(vehicleList2);
        List<Vehicle> copiedVehicles = car1.getVehicles();

        Vehicle copiedCar = copiedVehicles.get(0);
        System.out.println("Dostępność oryginalnego pojazdu (car1) przed zmianą: " + car1.isRented());
        copiedCar.setRented(true);
        System.out.println("Dostępność pojazdu po zmianie w kopii (copiedCar): " + copiedCar.isRented());
        System.out.println("Dostępność oryginalnego pojazdu (car1) po zmianie: " + car1.isRented());

        copiedCar.setRented(false);
        System.out.println("\nPo ponownej zmianie w kopii:");
        System.out.println("Dostępność oryginalnego pojazdu (car1): " + car1.isRented());
        System.out.println("Dostępność pojazdu w kopii (copiedCar): " + copiedCar.isRented());

        Vehicle copiedCar2 = copiedVehicles.get(1);
        copiedCar2.setRented(true);
        System.out.println("\n=== TEST: Drugi pojazd w kopii ===");
        System.out.println("Dostępność oryginalnego pojazdu (car2) przed zmianą: " + car2.isRented());
        System.out.println("Dostępność pojazdu po zmianie w kopii (copiedCar2): " + copiedCar2.isRented());
        System.out.println("Dostępność oryginalnego pojazdu (car2) po zmianie: " + car2.isRented());

        System.out.println("\n=== TEST: Wypisywanie pojazdów ===");
        System.out.println("Oryginalne pojazdy:");
        for (Vehicle vehicle : vehicleList2) {
            vehicle.displayInfo();
        }

        System.out.println("\nPojazdy w kopii:");
        for (Vehicle vehicle : copiedVehicles) {
            vehicle.displayInfo();
        }

        car1.saveToCsv(Paths.get("Vehicles.csv"));
    }

    private static Vehicle findVehicleById(List<Vehicle> vehicles, int carId) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarid() == carId) {
                return vehicle;
            }
        }
        return null;
    }
}