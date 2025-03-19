package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    private String login;
    private String password;
    private String role;
    private Vehicle rentedVehicle;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Vehicle getRentedVehicle() {
        return rentedVehicle;
    }

    public void setRentedVehicle(Vehicle rentedVehicle) {
        this.rentedVehicle = rentedVehicle;
    }

    @Override
    public String toString() {
        return "User{login='" + login + "', role='" + role + "', rentedVehicle=" + rentedVehicle + "}";
    }

    public static void registerUser(Path path, String login, String password) {
        User newUser = new User(login, DigestUtils.sha256Hex(password), "user", null);
        saveToCsv(path, newUser);
    }

    public String toCSV() {
        return login + ";" + password + ";" + role + ";" + (rentedVehicle != null ? rentedVehicle.getCarid() : "Brak pojazdu");
    }

    public static void saveToCsv(Path path, User... users) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("login;password;role;rentedVehicle\n");
            for (User user : users) {
                writer.write(user.toCSV() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User findUser(Path path, String login) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines.subList(1, lines.size())) {
                String[] data = line.split(";");
                if (data[0].equals(login)) {
                    return new User(data[0], data[1], data[2], null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        Vehicle vehicle = findVehicleById(vehicles, carid);
        if (vehicle != null) {
            vehicle.setPrice(newPrice);
            vehicle.setRented(isRented);
            System.out.println("Pojazd z ID " + carid + " został zaktualizowany.");
            vehicle.save();
        } else {
            System.out.println("Pojazd o podanym ID nie istnieje.");
        }
    }

    // Wyświetlanie użytkowników i przypisanych pojazdów
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

    private Vehicle findVehicleById(List<Vehicle> vehicles, int carid) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCarid() == carid) {
                return vehicle;
            }
        }
        return null;
    }


    public static List<User> loadFromCsv(Path path) {
        List<User> users = new ArrayList<>();
        try (Scanner scanner = new Scanner(path.toFile())) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(";");
                if (data.length < 4) {
                    System.out.println("Błąd w danych CSV (za mało danych): " + line);
                    continue;
                }

                String login = data[0].trim();
                String password = data[1].trim();
                String role = data[2].trim();
                Vehicle rentedVehicle = null;

                if (data.length > 3 && !data[3].equals("Brak pojazdu")) {
                    int carid = Integer.parseInt(data[3].trim());
                    rentedVehicle = findVehicleById(vehicles, carid);
                }

                users.add(new User(login, password, role, rentedVehicle));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

}