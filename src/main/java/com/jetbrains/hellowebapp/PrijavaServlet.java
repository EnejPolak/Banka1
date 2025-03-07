package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/prijava")
public class PrijavaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String geslo = request.getParameter("geslo");
        String hashiranoGeslo = hashirajGeslo(geslo);

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String sql = "SELECT geslo_hash FROM uporabniki WHERE email = ?";
        try (Connection povezava = PovezavaZBazo.connect();
             PreparedStatement stmt = povezava.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rezultat = stmt.executeQuery()) {
                if (!rezultat.next()) {
                    // Uporabnik s podano e-pošto ne obstaja
                    out.println("{\"status\":\"error\",\"field\":\"email\",\"message\":\"Neveljaven e-poštni naslov.\"}");
                    return;
                } else {
                    String hashIzBaze = rezultat.getString("geslo_hash");
                    if (!hashiranoGeslo.equals(hashIzBaze)) {
                        out.println("{\"status\":\"error\",\"field\":\"geslo\",\"message\":\"Nepravilno geslo.\"}");
                        return;
                    } else {
                        out.println("{\"status\":\"success\",\"message\":\"✅ Prijava uspešna! Dobrodošli, " + email + "\"}");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⛔ Napaka pri prijavi: " + e.getMessage());
            out.println("{\"status\":\"error\",\"field\":\"server\",\"message\":\"Napaka pri strežniku.\"}");
        }
    }

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
