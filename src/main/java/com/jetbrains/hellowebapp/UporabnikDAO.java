package com.jetbrains.hellowebapp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UporabnikDAO {

    /**
     * Registracija uporabnika preko stored procedure sp_registriraj_uporabnika.
     * Sprejme ime, e-pošto, geslo (v čisti obliki) in id države.
     *
     * @param ime      Ime uporabnika.
     * @param ePosta   E-poštni naslov.
     * @param geslo    Geslo v čisti obliki.
     * @param drzavaId Id države, ki ga je izbral uporabnik.
     * @return true, če je registracija uspešna, sicer false.
     */
    public static boolean registrirajUporabnika(String ime, String ePosta, String geslo, int drzavaId) {
        String gesloHash = hashirajGeslo(geslo);
        // Uporabljamo stored procedure, zato ne uporabljamo SQL INSERT neposredno.
        String callSQL = "{ ? = call sp_registriraj_uporabnika(?, ?, ?, ?) }";
        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            // Prvi parameter je izhodni, tip BOOLEAN.
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.setString(2, ime);
            cstmt.setString(3, ePosta);
            cstmt.setString(4, gesloHash);
            cstmt.setInt(5, drzavaId);

            cstmt.execute();
            return cstmt.getBoolean(1);
        } catch (SQLException e) {
            System.out.println("⛔ Napaka pri registraciji: " + e.getMessage());
            return false;
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

            // Prvi parameter je izhodni, tip INTEGER.
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setString(2, email);
            cstmt.setString(3, gesloHash);

            cstmt.execute();
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
