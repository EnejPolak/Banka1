package com.jetbrains.hellowebapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UporabnikDAO {

    // 🔹 Registracija uporabnika
    public static boolean registrirajUporabnika(String ime, String ePosta, String geslo) {
        if (preveriCeEPostaObstaja(ePosta)) {
            System.out.println("⛔ E-pošta že obstaja! Izberite drugo.");
            return false;
        }

        String gesloHash = zakodirajGeslo(geslo);
        String poizvedba = "INSERT INTO uporabniki (ime, email, geslo_hash) VALUES (?, ?, ?)";

        try (Connection povezava = PovezavaZBazo.connect();
             PreparedStatement stavek = povezava.prepareStatement(poizvedba)) {

            stavek.setString(1, ime);
            stavek.setString(2, ePosta);
            stavek.setString(3, gesloHash);

            int vrsticeVstavljene = stavek.executeUpdate();
            return vrsticeVstavljene > 0;

        } catch (SQLException e) {
            System.out.println("⛔ Napaka pri registraciji: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Preveri, ali e-pošta že obstaja v bazi
    private static boolean preveriCeEPostaObstaja(String ePosta) {
        String poizvedba = "SELECT id FROM uporabniki WHERE email = ?";

        try (Connection povezava = PovezavaZBazo.connect();
             PreparedStatement stavek = povezava.prepareStatement(poizvedba)) {

            stavek.setString(1, ePosta);
            ResultSet rezultat = stavek.executeQuery();
            return rezultat.next(); // Če obstaja vrstica, e-pošta že obstaja

        } catch (SQLException e) {
            System.out.println("⛔ Napaka pri preverjanju e-pošte: " + e.getMessage());
            return true;
        }
    }

    // 🔹 Funkcija za zakodiranje gesla
    private static String zakodirajGeslo(String geslo) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] zakodirano = md.digest(geslo.getBytes());
            StringBuilder zakodiranoBesedilo = new StringBuilder();
            for (byte b : zakodirano) {
                zakodiranoBesedilo.append(String.format("%02x", b));
            }
            return zakodiranoBesedilo.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("⛔ Napaka pri kodiranju gesla: " + e.getMessage());
            return null;
        }
    }
}
