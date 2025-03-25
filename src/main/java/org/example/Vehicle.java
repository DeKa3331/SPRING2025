package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public abstract class Vehicle implements IVehicleRepository {
    private List<Vehicle> vehicles = new ArrayList<>();
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;
    private int carid;

    public Vehicle(String brand, String model, int year, double price, boolean rented, int carid) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
        this.carid = carid;
    }

    public void setVehicles(List<Vehicle> vehiclesList) {
        this.vehicles = vehiclesList;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCarid(int carid) {
        this.carid = carid;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public int getCarid() {
        return carid;
    }

    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle vehicle : this.vehicles) {
            if (vehicle instanceof Car) {
                copy.add(new Car(vehicle.getBrand(), vehicle.getModel(), vehicle.getYear(), vehicle.getPrice(), vehicle.getCarid(), vehicle.isRented()));
            } else if (vehicle instanceof Motorcycle) {
                Motorcycle motorcycle = (Motorcycle) vehicle;
                copy.add(new Motorcycle(motorcycle.getBrand(), motorcycle.getModel(), motorcycle.getYear(), motorcycle.getPrice(), motorcycle.getCarid(), motorcycle.isRented(), motorcycle.getCategory()));
            }
        }
        return copy;

    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public  Vehicle findVehicleById(int carid) {
        for (Vehicle vehicle : this.vehicles) {
            if (vehicle.getCarid() == carid) {
                return vehicle;
            }
        }
        return null;
    }

    @Override
    public void rentVehicle(int carid) {
        Vehicle vehicle = findVehicleById(carid);
        if (vehicle != null) {
            if (!vehicle.isRented()) {
                vehicle.setRented(true);
                System.out.println("Pojazd o id " + carid + " został wynajęty.");

                save(); // Zapisz zmieniony pojazd
            } else {
                System.out.println("Pojazd o id " + carid + " jest już wynajęty.");
            }
        } else {
            System.out.println("Pojazd o id " + carid + " nie istnieje w naszej bazie.");
        }
    }

    @Override
    public void returnVehicle(int carid) {
        Vehicle vehicle = findVehicleById(carid);
        if (vehicle != null) {
            if (vehicle.isRented()) {
                vehicle.setRented(false);
                System.out.println("Pojazd o id " + carid + " został zwrócony.");
                save();
            } else {
                System.out.println("Pojazd o id " + carid + " nie był wynajęty.");
            }
        } else {
            System.out.println("Pojazd o id " + carid + " nie istnieje w naszej bazie.");
        }
    }

    @Override
    public void save() {
        saveToCsv(Paths.get("Vehicles.csv"));
    }

    public static Vehicle fromCSVLine(String line) {
        String[] parts = line.split(";");

        if (parts.length < 6) {
            System.out.println("Błąd w danych CSV (za mało danych): " + line);
            return null;
        }

        int carid = Integer.parseInt(parts[0].trim());
        String brand = parts[1].trim();
        String model = parts[2].trim();
        int year = Integer.parseInt(parts[3].trim());
        double price = Double.parseDouble(parts[4].trim());
        boolean rented = Boolean.parseBoolean(parts[5].trim());

        if (parts.length > 6) {
            String category = parts[6].trim();
            return new Motorcycle(brand, model, year, price, carid, rented, category);
        } else {
            return new Car(brand, model, year, price, carid, rented);
        }
    }

    public static List<Vehicle> fromCsv(Path path) {
        List<Vehicle> vehicleList = new ArrayList<>();
        try (Scanner scanner = new Scanner(path.toFile())) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Vehicle vehicle = Vehicle.fromCSVLine(line);
                vehicleList.add(vehicle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vehicleList;
    }

    public void saveToCsv(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("carid;brand;model;year;price;rented;category\n");
            for (Vehicle vehicle : this.vehicles) {
                writer.write(vehicle.toCSV() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void displayInfo();

    public String toCSV() {
        return carid + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }

    @Override
    public String toString() {
        return "Vehicle: Brand=" + brand + ", Model=" + model + ", Year=" + year + ", Price=" + price + ", Rented=" + rented + ", carid=" + carid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return year == vehicle.year &&
                Objects.equals(brand, vehicle.brand) &&
                Objects.equals(model, vehicle.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, year);
    }


    public boolean isEmpty() {
        return true;
    }
}
