package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/uporabnik")
public class UporabnikController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ime = request.getParameter("ime");
        String email = request.getParameter("email");
        String geslo = request.getParameter("geslo");

        if (email == null || !email.contains("@")) {
            response.getWriter().println("⛔ Napaka: Neveljaven e-poštni naslov.");
            return;
        }

        String hashiranoGeslo = hashirajGeslo(geslo); // Hash gesla

        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO uporabniki (ime, email, geslo_hash) VALUES (?, ?, ?)")) {

            stmt.setString(1, ime);
            stmt.setString(2, email);
            stmt.setString(3, hashiranoGeslo);

            stmt.executeUpdate();
            response.getWriter().println("✅ Registracija uspešna!");

        } catch (SQLException e) {
            response.getWriter().println("⛔ Napaka: " + e.getMessage());
        }
    }

    // Hash funkcija
    public static String hashirajGeslo(String geslo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(geslo.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Napaka pri hashiranju gesla!", e);
        }
    }
}
