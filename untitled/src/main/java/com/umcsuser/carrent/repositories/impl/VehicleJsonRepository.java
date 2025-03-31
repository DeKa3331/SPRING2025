package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.utils.JsonFileStorage;

import java.util.*;
import java.util.stream.Collectors;

public class VehicleJsonRepository implements VehicleRepository {
    private final JsonFileStorage<Vehicle> storage =
            new JsonFileStorage<>("vehicles.json", new TypeToken<List<Vehicle>>(){}.getType());
    private final List<Vehicle> vehicles;

    public VehicleJsonRepository() {
        this.vehicles = new ArrayList<>(storage.load());
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        } else {
            deleteById(vehicle.getId());
        }
        vehicles.add(vehicle);
        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
        storage.save(vehicles);
    }
}