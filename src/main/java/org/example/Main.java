package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/*
kuba 123
user
admin


 */

public class Main {
    public static void main(String[] args) {
        Path vehiclesPath = Paths.get("Vehicles.csv");
        List<Vehicle> vehicleList = Vehicle.fromCsv(vehiclesPath);

        if (vehicleList.isEmpty()) {
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
            }
        } else if (option == 2) {
            System.out.println("Podaj login: ");
            String login = scanner.nextLine();
            System.out.println("Podaj hasło: ");
            String password = scanner.nextLine();

            // Rejestracja użytkownika
            User newUser = new User(login, DigestUtils.sha256Hex(password), "uzytkownik", null);
            users.add(newUser);

            // Zapisanie zaktualizowanej listy użytkowników do pliku CSV
            User.saveToCsv(usersFilePath, users);

            System.out.println("Rejestracja zakończona sukcesem!");
        }

        // Sprawdź rolę i umożliwienie działań
        if (currentUser != null && currentUser.isAdmin()) {
            // Administrator może edytować pojazdy i wyświetlać użytkowników
            System.out.println("Wybierz opcję: 1 - Edytuj pojazd, 2 - Wyświetl użytkowników");
            int adminOption = scanner.nextInt();
            if (adminOption == 1) {
                System.out.println("Podaj ID pojazdu: ");
                int carid = scanner.nextInt();
                System.out.println("Podaj nową cenę: ");
                double price = scanner.nextDouble();
                System.out.println("Czy pojazd ma być wynajęty? (true/false): ");
                boolean isRented = scanner.nextBoolean();
                currentUser.editVehicle(vehicleList, carid, price, isRented);
            } else if (adminOption == 2) {
                currentUser.displayUsersWithVehicles(users);
            }
        } else if (currentUser != null && !currentUser.isAdmin()) {
            // Zwykły użytkownik może wypożyczyć pojazd
            System.out.println("Wybierz pojazd do wypożyczenia (wpisz ID): ");
            int vehicleId = scanner.nextInt();
            Vehicle vehicle = findVehicleById(vehicleList, vehicleId);

            if (vehicle != null && !vehicle.isRented()) {
                currentUser.setRentedVehicle(vehicle);
                vehicle.setRented(true);
                System.out.println("Pojazd wypożyczony: " + vehicle);
                vehicle.save(); // Zapisz zmiany w pliku CSV
            } else {
                System.out.println("Pojazd jest już wypożyczony lub nie istnieje.");
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