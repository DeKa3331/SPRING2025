package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.utils.JdbcConnectionManager;

import java.sql.*;
import java.util.*;

public class RentalJdbcRepository implements RentalRepository {

    @Override
    public List<Rental> findAll() {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT * FROM rental";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Rental rental = Rental.builder()
                        .id(rs.getString("id"))
                        .vehicleId(rs.getString("vehicle_id"))
                        .userId(rs.getString("user_id"))
                        .rentDateTime(rs.getString("rent_date"))
                        .returnDateTime(rs.getString("return_date"))
                        .build();
                list.add(rental);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
        return list;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rental rental = Rental.builder()
                            .id(rs.getString("id"))
                            .vehicleId(rs.getString("vehicle_id"))
                            .userId(rs.getString("user_id"))
                            .rentDateTime(rs.getString("rent_date"))
                            .returnDateTime(rs.getString("return_date"))
                            .build();
                    return Optional.of(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Rental> findByVehicleId(String vehicleId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE vehicle_id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = Rental.builder()
                            .id(rs.getString("id"))
                            .vehicleId(rs.getString("vehicle_id"))
                            .userId(rs.getString("user_id"))
                            .rentDateTime(rs.getString("rent_date"))
                            .returnDateTime(rs.getString("return_date"))
                            .build();
                    rentals.add(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals by vehicle", e);
        }
        return rentals;
    }

    @Override
    public List<Rental> findByUserId(String userId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE user_id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rental rental = Rental.builder()
                            .id(rs.getString("id"))
                            .vehicleId(rs.getString("vehicle_id"))
                            .userId(rs.getString("user_id"))
                            .rentDateTime(rs.getString("rent_date"))
                            .returnDateTime(rs.getString("return_date"))
                            .build();
                    rentals.add(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals by user", e);
        }
        return rentals;
    }

    @Override
    public Rental save(Rental rental) {
        String sql;
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
            sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE rental SET vehicle_id = ?, user_id = ?, rent_date = ?, return_date = ? WHERE id = ?";
        }

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (sql.startsWith("INSERT")) {
                stmt.setString(1, rental.getId());
                stmt.setString(2, rental.getVehicleId());
                stmt.setString(3, rental.getUserId());
                stmt.setTimestamp(4, Timestamp.valueOf(rental.getRentDateTime()));
                stmt.setTimestamp(5, rental.getReturnDateTime() != null ?
                        Timestamp.valueOf(rental.getReturnDateTime()) : null);
            } else {
                stmt.setString(1, rental.getVehicleId());
                stmt.setString(2, rental.getUserId());
                stmt.setTimestamp(3, Timestamp.valueOf(rental.getRentDateTime()));
                stmt.setTimestamp(4, rental.getReturnDateTime() != null ?
                        Timestamp.valueOf(rental.getReturnDateTime()) : null);
                stmt.setString(5, rental.getId());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        }
    }
}
