package org.example;

import java.util.List;

class Motorcycle extends Vehicle {
    String category;

    public Motorcycle(String brand, String model, int year, double price, int carid, boolean rented, String category) {
        super(brand, model, year, price,rented, carid);
        this.category = category;
    }
    @Override
    public String toString() {
        return super.toString() + ", Category=" + category;
    }
    @Override
    public String toCSV() {
        return super.toCSV() + ";" + category;
    }
    @Override
    public void displayInfo() {
        System.out.println(this.toString());
    }
    @Override
    public void save() {

    }
    public String getCategory() {
        return category;
    }
}