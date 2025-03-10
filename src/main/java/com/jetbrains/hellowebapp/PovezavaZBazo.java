package com.jetbrains.hellowebapp;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PovezavaZBazo {
    // Naloži .env datoteko, ki je na classpathu (v src/main/resources)
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");
    private static final String DRIVER_NAME = "org.postgresql.Driver";

    public static Connection connect() {
        try {
            // Naloži gonilnik
            Class.forName(DRIVER_NAME);
            // Ustvari povezavo z uporabo podatkov iz .env
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Povezava z bazo je uspešna!");
            return con;
        } catch (ClassNotFoundException e) {
            System.err.println("⛔ JDBC gonilnik ni bil najden: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("⛔ Napaka pri povezavi z bazo: " + e.getMessage());
        }
        return null;
    }

    // Glavna metoda za testiranje povezave
    public static void main(String[] args) {
        Connection c = connect();
        if (c != null) {
            System.out.println("Povezava je uspela!");
        } else {
            System.out.println("Povezava ni uspela!");
        }
    }
}
