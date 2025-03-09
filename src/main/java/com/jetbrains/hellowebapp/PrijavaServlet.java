package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Dodajte ta import!
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
        // Pridobi podatke iz obrazca
        String email = request.getParameter("email");
        String geslo = request.getParameter("geslo");

        // Hashiraj geslo
        String hashiranoGeslo = hashirajGeslo(geslo);

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Prilagojena SQL poizvedba, ki vrne tudi uporabniški ID
        String sql = "SELECT id, geslo_hash FROM uporabniki WHERE email = ?";
        try (Connection povezava = PovezavaZBazo.connect();
             PreparedStatement stmt = povezava.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rezultat = stmt.executeQuery()) {
                if (!rezultat.next()) {
                    out.println("{\"status\":\"error\", \"field\":\"email\", \"message\":\"Neveljaven e-poštni naslov\"}");
                    return;
                } else {
                    int uporabnikId = rezultat.getInt("id");
                    String hashIzBaze = rezultat.getString("geslo_hash");
                    if (!hashiranoGeslo.equals(hashIzBaze)) {
                        out.println("{\"status\":\"error\", \"field\":\"geslo\", \"message\":\"Nepravilno geslo\"}");
                        return;
                    } else {
                        // Ustvari sejo in shrani uporabniški ID (uporabite Integer.valueOf, če se pojavijo težave)
                        HttpSession session = request.getSession();
                        session.setAttribute("uporabnikId", Integer.valueOf(uporabnikId));
                        out.println("{\"status\":\"success\", \"message\":\"success\"}");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⛔ Napaka pri prijavi: " + e.getMessage());
            out.println("{\"status\":\"error\", \"field\":\"server\", \"message\":\"Napaka pri strežniku\"}");
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


