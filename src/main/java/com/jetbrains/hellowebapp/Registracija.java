package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/registracija")
public class Registracija extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Pridobi podatke
        String ime   = request.getParameter("ime");
        String email = request.getParameter("email");
        String geslo = request.getParameter("geslo");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Preveri polja
        if (ime == null || ime.trim().isEmpty()) {
            out.println("{\"status\":\"error\", \"field\":\"ime\", \"message\":\"Ime ne sme biti prazno.\"}");
            return;
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            out.println("{\"status\":\"error\", \"field\":\"email\", \"message\":\"Neveljaven e-poštni naslov.\"}");
            return;
        }
        if (geslo == null || geslo.trim().isEmpty()) {
            out.println("{\"status\":\"error\", \"field\":\"geslo\", \"message\":\"Geslo ne sme biti prazno.\"}");
            return;
        }

        // Klic v bazo
        try {
            boolean uspeh = UporabnikDAO.registrirajUporabnika(ime, email, geslo);
            if (uspeh) {
                out.println("{\"status\":\"success\"}");
            } else {
                // E-pošta že obstaja
                out.println("{\"status\":\"error\", \"field\":\"email\", \"message\":\"E-pošta že obstaja!\"}");
            }
        } catch (Exception e) {
            out.println("{\"status\":\"error\", \"field\":\"server\", \"message\":\"Napaka pri registraciji: " + e.getMessage() + "\"}");
        }
    }
}

