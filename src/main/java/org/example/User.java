package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private static List<User> userList = new ArrayList<>();
    private static VehicleRepository vehicleRepository = new VehicleRepository();

    private String login;
    private String password;
    private String role;
    private Vehicle rentedVehicle;

    private static User currentUser;

    public User(String login, String password, String role, Vehicle rentedVehicle) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicle = rentedVehicle;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Vehicle getRentedVehicle() {
        return rentedVehicle;
    }

    public void setRentedVehicle(Vehicle rentedVehicle) {
        this.rentedVehicle = rentedVehicle;

        for (User user : userList) {
            if (user.getLogin().equals(this.login)) {
                user.rentedVehicle = rentedVehicle;
                break;
            }
        }

        Path path = Paths.get("Accounts.csv");
        saveToCsv(path, userList);
    }







    @Override
    public String toString() {
        return "User{login='" + login + "', role='" + role + "', rentedVehicle=" + rentedVehicle + "}";
    }

    public String toCSV() {
        return login + ";" + password + ";" + role + ";" + (rentedVehicle != null ? rentedVehicle.getCarid() : "Brak pojazdu");
    }

    public static void saveToCsv(Path path, List<User> users) {
        if (users.isEmpty()) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("login;password;role;rentedVehicle\n");
            for (User user : users) {
                writer.write(user.toCSV() + "\n");
            }
        } catch (IOException e) {
        }
    }




    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User findUser(Path path, String login) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines.subList(1, lines.size())) {
                String[] data = line.split(";");
                if (data.length < 4) {
                    continue;
                }

                if (data[0].equals(login)) {
                    Vehicle rentedVehicle = null;
                    if (!data[3].equals("Brak pojazdu")) {
                        try {
                            int carid = Integer.parseInt(data[3].trim());
                            rentedVehicle = findVehicleById(carid);
                            if (rentedVehicle == null) {
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    return new User(data[0], data[1], data[2], rentedVehicle);
                }
            }
        } catch (IOException e) {
        }
        return null;
    }





    public boolean isAdmin() {
        return "admin".equals(this.role);
    }

    public void editVehicle(List<Vehicle> vehicles, int carid, double newPrice, boolean isRented) {
        if (!isAdmin()) {
            System.out.println("Tylko administrator może edytować pojazdy.");
            return;
        }
        Vehicle vehicle = findVehicleById(carid);
        if (vehicle != null) {
            vehicle.setPrice(newPrice);
            vehicle.setRented(isRented);
            System.out.println("Pojazd z ID " + carid + " został zaktualizowany.");
            vehicle.save();
        } else {
            System.out.println("Pojazd o podanym ID nie istnieje.");
        }
    }

    public void displayUsersWithVehicles(List<User> users) {
        if (!isAdmin()) {
            System.out.println("Tylko administrator może wyświetlać użytkowników.");
            return;
        }
        for (User user : users) {
            System.out.println("Użytkownik: " + user.getLogin());
            if (user.getRentedVehicle() != null) {
                System.out.println("Wypożyczony pojazd: " + user.getRentedVehicle());
            } else {
                System.out.println("Brak wypożyczonego pojazdu.");
            }
        }
    }

    private static Vehicle findVehicleById(int carid) {
        return vehicleRepository.findVehicleById(carid);
    }

    public static List<User> loadFromCsv(Path path) {
        userList.clear();
        try (Scanner scanner = new Scanner(path.toFile())) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(";");
                String login = data[0].trim();
                String password = data[1].trim();
                String role = data[2].trim();
                Vehicle rentedVehicle = null;

                if (!data[3].equals("Brak pojazdu")) {
                    try {
                        int carid = Integer.parseInt(data[3].trim());
                        rentedVehicle = findVehicleById(carid);
                    } catch (NumberFormatException e) {
                        System.out.println("Niepoprawny numer ID pojazdu: " + data[3]);
                    }
                }

                User user = new User(login, password, role, rentedVehicle);
                userList.add(user);
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas odczytu pliku: " + e.getMessage());
        }
        return userList;
    }



}