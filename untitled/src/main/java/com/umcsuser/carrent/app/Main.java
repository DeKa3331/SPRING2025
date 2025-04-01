package com.umcsuser.carrent.app;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

import java.util.Optional;
import java.util.Scanner;

/*
L:admin
H:admin123

L:user1
H:user123

*/

public class Main {
    public static void main(String[] args) {
        // Inicjalizacja repozytoriów
        UserRepository userRepository = new UserJsonRepository();
        VehicleRepository vehicleRepository = new VehicleJsonRepository();
        RentalRepository rentalRepository = new RentalJsonRepository();

        // Inicjalizacja serwisów
        AuthService authService = new AuthService(userRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository);
        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository);
        AdminService adminService = new AdminService(vehicleRepository, userRepository, rentalRepository);
        UserService userService = new UserService(vehicleRepository, rentalService);

        Scanner scanner = new Scanner(System.in);
        Optional<User> loggedInUser = null;
        User currentUser = null;

        while (currentUser ==null) {
            System.out.println("Wybierz opcję: 1 - Zaloguj, 2 - Zarejestruj, 3 - Wyjdź");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.println("Podaj login:");
                String login = scanner.nextLine();
                System.out.println("Podaj hasło:");
                String password = scanner.nextLine();
                loggedInUser = authService.login(login, password);

                if (loggedInUser.isPresent()) {
                    System.out.println("Zalogowano jako: " + loggedInUser.get().getLogin());
                    break;
                } else {
                    System.out.println("Błędne dane logowania!");
                }
            } else if (choice == 2) {
                System.out.println("Podaj login:");
                String login = scanner.nextLine();
                System.out.println("Podaj hasło:");
                String password = scanner.nextLine();
                authService.register(login, password, "USER");
                System.out.println("Rejestracja zakończona sukcesem!");
            } else if (choice == 3) {
                System.out.println("Zamykanie aplikacji...");
                return;
            } else {
                System.out.println("Niepoprawny wybór, spróbuj ponownie.");
            }
        }

        User user = loggedInUser.get();
        currentUser=user;
        if (user.getRole().equals("ADMIN")) {
            adminMenu(adminService, scanner);
        } else {
            userMenu(userService, currentUser, scanner);
        }
    }

    private static void adminMenu(AdminService adminService, Scanner scanner)    {
        while (true) {
            System.out.println("ADMIN MENU: 1 - Dodaj pojazd, 2 - Edytuj pojazd, 3 - Wyświetl wynajmy, 4 - Wyloguj");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> adminService.addVehicle(scanner);
                case 2 -> adminService.editVehicle(scanner);
                case 3 -> adminService.listAllRentals();
                case 4 -> {
                    System.out.println("Wylogowano.");
                    return;
                }
                default -> System.out.println("Niepoprawny wybór!");
            }
        }
    }

    private static void userMenu(UserService userService, User currentUser, Scanner scanner) {
        while (true) {
            System.out.println("USER MENU: 1 - Wypożycz pojazd, 2 - Zwróć pojazd, 3 - Pokaz dostepne,4-exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> userService.rentVehicle(currentUser, scanner);
                case 2 -> userService.returnVehicle(currentUser);
                case 3 -> userService.listAvailableVehicles();

                case 4 -> {
                    System.out.println("Wylogowano.");
                    return;
                }
                default -> System.out.println("Niepoprawny wybór!");
            }
        }
    }
}
