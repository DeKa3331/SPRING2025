package org.example;

public class User {
    private String login;
    private String password;
    private String role;
    private Vehicle rentedVehicle;

    public User(String login, String password, String role, Car rentedVehicle) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicle = rentedVehicle;
    }

    // Gettery i settery
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

    public void setRentedCar(Car rentedCar) {
        this.rentedVehicle = rentedCar;
    }

    @Override
    public String toString() {
        return "User{login='" + login + "', role='" + role + "', rentedCar=" + rentedVehicle + "}";
    }
}
