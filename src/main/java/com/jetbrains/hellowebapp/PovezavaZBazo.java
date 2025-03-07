package com.jetbrains.hellowebapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PovezavaZBazo {
    private static final String URL = "jdbc:postgresql://ep-tiny-math-a8scmk0c-pooler.eastus2.azure.neon.tech/neondb";
    private static final String USER = "neondb_owner";
    private static final String PASS = "npg_NVafpKP87qFW";
    private static final String DRIVER_NAME = "org.postgresql.Driver";

    public static Connection connect() {
        try {
            // Naloži gonilnik
            Class.forName(DRIVER_NAME);
            // Ustvari povezavo z uporabo URL, USER in PASS
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