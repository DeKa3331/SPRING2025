package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.*;
import com.umcsuser.carrent.repositories.*;

import java.util.Optional;
import java.util.Scanner;

public class AdminService {
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;

    public AdminService(VehicleRepository vehicleRepo,
                        UserRepository userRepo,
                        RentalRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
    }

    public void addVehicle(Scanner scanner) {
        System.out.println("Podaj kategorię pojazdu:");
        String category = scanner.nextLine();

        System.out.println("Podaj markę pojazdu:");
        String brand = scanner.nextLine();

        System.out.println("Podaj model pojazdu:");
        String model = scanner.nextLine();

        System.out.println("Podaj rok produkcji pojazdu:");
        int year = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Podaj numer rejestracyjny pojazdu:");
        String plate = scanner.nextLine();

        Vehicle vehicle = Vehicle.builder()
                .category(category)
                .brand(brand)
                .model(model)
                .year(year)
                .plate(plate)
                .build();

        System.out.println("Czy chcesz dodać atrybuty? (tak/nie)");
        if (scanner.nextLine().equalsIgnoreCase("tak")) {
            while (true) {
                System.out.println("Podaj nazwę atrybutu (lub wpisz 'koniec', aby zakończyć):");
                String key = scanner.nextLine();
                if (key.equalsIgnoreCase("koniec")) break;

                System.out.println("Podaj wartość atrybutu:");
                String value = scanner.nextLine();
                vehicle.addAttribute(key, value);
            }
        }

        vehicleRepo.save(vehicle);
        System.out.println("Dodano nowy pojazd: " + vehicle);
    }



public void editVehicle(Scanner scanner) {
        System.out.println("Podaj ID pojazdu do edycji:");
        String id = scanner.nextLine();

        Optional<Vehicle> vehicleOpt = vehicleRepo.findById(id);
        if (vehicleOpt.isEmpty()) {
            System.out.println("Pojazd nie istnieje!");
            return;
        }

        Vehicle vehicle = vehicleOpt.get();

        System.out.println("Co chcesz zmienić?");
        System.out.println("1 - Marka, 2 - Model, 3 - Rok, 4 - Numer rejestracyjny, 5 - Atrybuty");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.println("Nowa marka:");
                vehicle.setBrand(scanner.nextLine());
                break;
            case 2:
                System.out.println("Nowy model:");
                vehicle.setModel(scanner.nextLine());
                break;
            case 3:
                System.out.println("Nowy rok:");
                vehicle.setYear(scanner.nextInt());
                scanner.nextLine();
                break;
            case 4:
                System.out.println("Nowy numer rejestracyjny:");
                vehicle.setPlate(scanner.nextLine());
                break;
            case 5:
                editAttributes(vehicle, scanner);
                break;
            default:
                System.out.println("Nieprawidłowy wybór");
        }

        vehicleRepo.save(vehicle);
        System.out.println("Pojazd zaktualizowany!");
    }

    private void editAttributes(Vehicle vehicle, Scanner scanner) {
        System.out.println("Aktualne atrybuty: " + vehicle.getAttributes());
        System.out.println("1 - Dodaj atrybut, 2 - Usuń atrybut");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.println("Nazwa atrybutu:");
            String key = scanner.nextLine();
            System.out.println("Wartość atrybutu:");
            String value = scanner.nextLine();
            vehicle.addAttribute(key, value);
        } else if (choice == 2) {
            System.out.println("Nazwa atrybutu do usunięcia:");
            String key = scanner.nextLine();
            vehicle.removeAttribute(key);
        }
    }

    public void listAllRentals() {
        rentalRepo.findAll().forEach(System.out::println);
    }
}