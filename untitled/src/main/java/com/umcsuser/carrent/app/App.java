package com.umcsuser.carrent.app;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.services.*;

import java.util.Optional;
import java.util.Scanner;

public class App {
    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final AdminService adminService;
    private final VehicleRepository vehicleRepo;

    public App(AuthService authService, VehicleService vehicleService, RentalService rentalService,AdminService adminService,VehicleRepository vehicleRepo) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.adminService= adminService;
        this.vehicleRepo=vehicleRepo;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Optional<User> loggedInUser = null;
        User currentUser = null;

        while (currentUser == null) {
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
        System.out.println("User role: " + loggedInUser.get().getRole());
        currentUser = user;
        if (user.getRole().equals("ADMIN")) {
            adminMenu(scanner);
        } else {
            userMenu(scanner,currentUser);
        }
    }

    private void adminMenu(Scanner scanner) {
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

    private void userMenu(Scanner scanner,User currentUser) {
        UserService userService = new UserService(vehicleRepo, rentalService);
        while (true) {
            System.out.println("USER MENU: 1 - Wypożycz pojazd, 2 - Zwróć pojazd, 3 - Pokaż dostępne pojazdy, 4 - Wyjdź");
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
