package org.example;

import java.util.List;

public interface IVehicleRepository {
    void rentVehicle(int carid);
    void returnVehicle(int carid);
    void save();

    static List<Vehicle> getVehicles()
    {
        return null;
    }
}

