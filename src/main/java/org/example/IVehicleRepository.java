package org.example;

import java.util.List;

public interface IVehicleRepository {
    void rentVehicle(int carid);
    void returnVehicle(int carid);
    void save();
    void addVehicle(Vehicle vehicle);
    void removeVehicle(int carId);
    List<Vehicle> getVehicles();

    Vehicle findVehicleById(int carid);

    int generateCarId();
}
