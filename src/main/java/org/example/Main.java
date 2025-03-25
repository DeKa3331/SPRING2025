package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Stworzenie repozytorium pojazdów, które obsługuje operacje na pojazdach
        IVehicleRepository vehicleRepo = new VehicleRepository();

        // Załaduj pojazdy z pliku CSV
        Path vehiclesPath = Paths.get("Vehicles.csv");
        vehicleRepo.save();  // Wczytuje pojazdy z pliku do repozytorium

        // Sprawdzenie, czy repozytorium zawiera pojazdy
        if (vehicleRepo.getVehicles().isEmpty()) {
            System.out.println("Brak pojazdów w bazie!");
        }

        Path usersFilePath = Paths.get("Accounts.csv");
        List<User> users = User.loadFromCsv(usersFilePath);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Wybierz opcję: 1 - Zaloguj, 2 - Zarejestruj");
        int option = scanner.nextInt();
        scanner.nextLine();

        User currentUser = null;

        if (option == 1) {
            System.out.println("Podaj login: ");
            String login = scanner.nextLine();
            System.out.println("Podaj hasło: ");
            String password = scanner.nextLine();

            currentUser = Authentication.login(login, password);
            if (currentUser == null) {
                System.out.println("Błędny login lub hasło!");
                return;
            } else {
                System.out.println("Zalogowano jako: " + currentUser.getLogin());
                User.setCurrentUser(currentUser);
            }
        } else if (option == 2) {
            System.out.println("Podaj login: ");
            String login = scanner.nextLine();
            System.out.println("Podaj hasło: ");
            String password = scanner.nextLine();

            User newUser = new User(login, DigestUtils.sha256Hex(password), "uzytkownik", null);
            users.add(newUser);
            User.saveToCsv(usersFilePath, users);

            System.out.println("Rejestracja zakończona sukcesem!");
        }

        if (currentUser != null && currentUser.isAdmin()) {
            System.out.println("Wybierz opcję: 1 - Edytuj pojazd, 2 - Wyświetl użytkowników, 3 - Wyświetl stan pojazdów");
            int adminOption = scanner.nextInt();
            if (adminOption == 1) {
                System.out.println("Podaj ID pojazdu: ");
                int carid = scanner.nextInt();
                System.out.println("Podaj nową cenę: ");
                double price = scanner.nextDouble();
                System.out.println("Czy pojazd ma być wynajęty? (true/false): ");
                boolean isRented = scanner.nextBoolean();
                currentUser.editVehicle(vehicleRepo.getVehicles(), carid, price, isRented);
            } else if (adminOption == 2) {
                currentUser.displayUsersWithVehicles(users);
            } else if (adminOption == 3) {
                System.out.println("Aktualny stan pojazdów:");
                for (Vehicle vehicle : vehicleRepo.getVehicles()) {
                    System.out.println(vehicle);
                }
            }
        } else if (currentUser != null && !currentUser.isAdmin()) {
            System.out.println("Wybierz opcję: 1 - Wypożycz pojazd, 2 - Zwróć pojazd, 3 - Wyświetl dostępne pojazdy");
            int userOption = scanner.nextInt();

            if (userOption == 1) {
                System.out.println("Wybierz pojazd do wypożyczenia (wpisz ID): ");
                int vehicleId = scanner.nextInt();
                Vehicle vehicle = findVehicleById(vehicleRepo.getVehicles(), vehicleId);

                if (vehicle != null && !vehicle.isRented()) {
                    currentUser.setRentedVehicle(vehicle);
                    vehicleRepo.rentVehicle(vehicleId);
                    vehicle.save();
                    User.saveToCsv(usersFilePath, users);
                } else {
                    System.out.println("Pojazd jest już wypożyczony lub nie istnieje.");
                }
            }
            else if (userOption == 2) {
                Vehicle rentedVehicle = currentUser.getRentedVehicle();
                if (rentedVehicle != null) {
                    rentedVehicle.setRented(false);
                    System.out.println("Pojazd zwrócony: " + rentedVehicle);
                    currentUser.setRentedVehicle(null);
                    rentedVehicle.save();
                } else {
                    System.out.println("Nie wypożyczono żadnego pojazdu.");
                }
            }
            else if (userOption == 3) {
                System.out.println("Dostępne pojazdy:");
                for (Vehicle vehicle : vehicleRepo.getVehicles()) {
                    if (!vehicle.isRented()) {
                        System.out.println(vehicle);
                    }
                }
            }
        }
    }

    private static Vehicle findVehicleById(List<Vehicle> vehicleList, int vehicleId) {
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getCarid() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }
}
