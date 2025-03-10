package com.jetbrains.hellowebapp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UporabnikDAO {

    /**
     * Registracija uporabnika preko stored procedure sp_registriraj_uporabnika.
     * Vrne statusno kodo:
     * 0: Uspešno
     * 1: Email že obstaja
     * -1: Druga napaka
     */
    public static int registrirajUporabnika(String ime, String ePosta, String geslo, int drzavaId) {
        String gesloHash = hashirajGeslo(geslo);
        String callSQL = "{ ? = call sp_registriraj_uporabnika(?, ?, ?, ?) }";

        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            // Registriraj izhodni parameter (INTEGER)
            cstmt.registerOutParameter(1, Types.INTEGER);

            // Nastavi vhodne parametre
            cstmt.setString(2, ime);
            cstmt.setString(3, ePosta);
            cstmt.setString(4, gesloHash);
            cstmt.setInt(5, drzavaId);

            // Izvedi stored procedure
            cstmt.execute();

            // Vrni statusno kodo
            return cstmt.getInt(1);

        } catch (SQLException e) {
            System.out.println("⛔ SQL Napaka pri registraciji: " + e.getMessage());
            return -1;
        }
    }



    /**
     * Prijava uporabnika preko stored procedure sp_prijava.
     */
    public static int prijava(String email, String geslo) {
        String gesloHash = hashirajGeslo(geslo);
        String callSQL = "{ ? = call sp_prijava(?, ?) }";

        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            // Registriraj izhodni parameter (INTEGER - uporabnikId)
            cstmt.registerOutParameter(1, Types.INTEGER);

            // Nastavi vhodne parametre
            cstmt.setString(2, email);
            cstmt.setString(3, gesloHash);

            // Izvedi stored procedure
            cstmt.execute();

            // Vrni uporabnikId ali -1, če prijava ni uspela
            return cstmt.getInt(1);

        } catch (SQLException e) {
            System.out.println("⛔ Napaka pri prijavi: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Funkcija za zakodiranje gesla z uporabo SHA-256.
     */
    public static String hashirajGeslo(String geslo) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(geslo.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("⛔ Napaka pri kodiranju gesla: " + e.getMessage());
            return null;
        }
    }
}
