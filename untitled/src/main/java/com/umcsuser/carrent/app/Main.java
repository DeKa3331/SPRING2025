package com.umcsuser.carrent.app;

import com.umcsuser.carrent.app.App;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.AdminService;
import com.umcsuser.carrent.services.AuthService;
import com.umcsuser.carrent.services.RentalService;
import com.umcsuser.carrent.services.VehicleService;

public class Main {
    public static void main(String[] args) {
        //String storageType = args.length > 0 ? args[0] : "json";

        /*
        L:admin
        H;admin123

        L:Kuba
        H:123


         */
        String storageType = "jdbc";

        UserRepository userRepo;
        VehicleRepository vehicleRepo;
        RentalRepository rentalRepo;



        switch (storageType) {
            case "jdbc" -> {
                userRepo = new UserJdbcRepository();
                vehicleRepo = new VehicleJdbcRepository();
                rentalRepo = new RentalJdbcRepository();
            }
            case "json" -> {
                userRepo = new UserJsonRepository();
                vehicleRepo = new VehicleJsonRepository();
                rentalRepo = new RentalJsonRepository();
            }
            default -> throw new IllegalArgumentException("Unknown storage type: " + storageType);
        }

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo);
        RentalService rentalService = new RentalService(rentalRepo,vehicleRepo);
        AdminService adminService = new AdminService(vehicleRepo,userRepo,rentalRepo);

        App app = new App(authService, vehicleService, rentalService,adminService);
        app.run();
    }
}
