package com.umcsuser.carrent.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//TODO:ustawienie zmiennej srodowiskowej DB_URL

//run->edit conf->enviroment variable->setujemy value
public class JdbcConnectionManager {
    private static JdbcConnectionManager instance;
    private final String url;

    public static JdbcConnectionManager getInstance() {
        if (instance == null) {
            instance = new JdbcConnectionManager();
        }
        return instance;
    }

    private JdbcConnectionManager() {
        url = System.getenv("DB_URL");
        if (url == null) {
            throw new RuntimeException("DB_URL not set!");
        }
    }
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Connection Failed!", e);
        }
    }
}