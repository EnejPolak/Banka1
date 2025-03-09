package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

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

        // Kliči stored procedure sp_prijava namesto SELECT
        String callSQL = "{ ? = call sp_prijava(?, ?) }";
        try (Connection povezava = PovezavaZBazo.connect();
             CallableStatement cstmt = povezava.prepareCall(callSQL)) {

            // Registriraj izhodni parameter (vrne INT)
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setString(2, email);
            cstmt.setString(3, hashiranoGeslo);

            cstmt.execute();
            int uporabnikId = cstmt.getInt(1);
            if (uporabnikId == -1) {
                out.println("{\"status\":\"error\", \"field\":\"email\", \"message\":\"Neveljaven e-poštni naslov ali geslo\"}");
                return;
            } else {
                // Ustvari sejo in shrani uporabniški ID
                HttpSession session = request.getSession();
                session.setAttribute("uporabnikId", Integer.valueOf(uporabnikId));
                out.println("{\"status\":\"success\", \"message\":\"Prijava uspešna\"}");
            }
        } catch (SQLException e) {
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
