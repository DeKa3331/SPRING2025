package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.utils.JsonFileStorage;

import java.util.*;
import java.util.stream.Collectors;

public class RentalJsonRepository implements RentalRepository {
    private final JsonFileStorage<Rental> storage =
            new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>(){}.getType());
    private final List<Rental> rentals;

    public RentalJsonRepository() {
        this.rentals = new ArrayList<>(storage.load());
    }

    public List<Rental> findAll() {
        return new ArrayList<>(rentals);
    }

    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<Rental> findByVehicleId(String vehicleId) {
        return rentals.stream()
                .filter(r -> r.getVehicleId().equals(vehicleId))
                .collect(Collectors.toList());
    }

    public List<Rental> findByUserId(String userId) {
        return rentals.stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }


    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        } else {
            deleteById(rental.getId());
        }
        rentals.add(rental);
        storage.save(rentals);
        return rental;
    }

    public void deleteById(String id) {
        rentals.removeIf(r -> r.getId().equals(id));
        storage.save(rentals);
    }
}