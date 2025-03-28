package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepo = new VehicleRepository();

        Path vehiclesPath = Paths.get("Vehicles.csv");
        vehicleRepo.save();

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
            System.out.println("Wybierz opcję: 1 - Dodaj pojazd, 2 - Usuń pojazd, 3 - Wyświetl użytkowników, 4 - Wyświetl stan pojazdów");
            int adminOption = scanner.nextInt();
            scanner.nextLine();

            if (adminOption == 1) {
                System.out.println("Podaj markę pojazdu: ");
                String brand = scanner.nextLine();
                System.out.println("Podaj model pojazdu: ");
                String model = scanner.nextLine();
                System.out.println("Podaj rok produkcji: ");
                int year = scanner.nextInt();
                System.out.println("Podaj cenę pojazdu: ");
                double price = scanner.nextDouble();
                System.out.println("Czy pojazd ma być wynajęty? (true/false): ");
                boolean isRented = scanner.nextBoolean();
                scanner.nextLine();

                int carid = generateCarId();  // Można dodać metodę generującą unikalne ID pojazdu
                Vehicle newVehicle = new Car(brand, model, year, price, carid, isRented);  // Stwórz nowy pojazd
                vehicleRepo.addVehicle(newVehicle);  // Dodaj pojazd do repozytorium
                vehicleRepo.save();  // Zapisz pojazdy do pliku
                System.out.println("Pojazd dodany pomyślnie!");
            } else if (adminOption == 2) {
                System.out.println("Podaj ID pojazdu do usunięcia: ");
                int carid = scanner.nextInt();
                Vehicle vehicleToRemove = vehicleRepo.findVehicleById(carid);
                if (vehicleToRemove != null) {
                    vehicleRepo.removeVehicle(vehicleToRemove.getCarid());
                    vehicleRepo.save();
                    System.out.println("Pojazd usunięty pomyślnie.");
                } else {
                    System.out.println("Pojazd o podanym ID nie istnieje.");
                }
            } else if (adminOption == 3) {
                System.out.println("Lista użytkowników:");
                for (User user : users) {
                    System.out.println(user);
                }
            } else if (adminOption == 4) {
                System.out.println("Aktualny stan pojazdów:");
                for (Vehicle vehicle : vehicleRepo.getVehicles()) {
                    System.out.println(vehicle);
                }
            }
        } else if (currentUser != null && !currentUser.isAdmin()) {
            System.out.println("Wybierz opcję: 1 - Wypożycz pojazd, 2 - Zwróć pojazd, 3 - Wyświetl dostępne pojazdy");
            int userOption = scanner.nextInt();
            scanner.nextLine();

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
            } else if (userOption == 2) {
                Vehicle rentedVehicle = currentUser.getRentedVehicle();

                if (rentedVehicle != null) {
                    rentedVehicle.returnVehicle(rentedVehicle.getCarid());
                    currentUser.setRentedVehicle(null);
                    vehicleRepo.returnVehicle(rentedVehicle.getCarid());
                    User.saveToCsv(usersFilePath, users);
                    vehicleRepo.save();
                } else {
                    System.out.println("Nie masz wypożyczonego pojazdu.");
                }
            } else if (userOption == 3) {
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

    private static int generateCarId() {
        return (int) (Math.random() * 1000);
    }

}
