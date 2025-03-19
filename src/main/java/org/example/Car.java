package org.example;

import java.util.List;

class Car extends Vehicle {
    public Car(String brand, String model, int year, double price, int carid, boolean rented) {
        super(brand, model, year, price,rented, carid);
    }

    @Override
    public void displayInfo() {
        System.out.println(this.toString());
    }


    @Override
    public void save() {

    }
}