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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Testna metoda za GET zahtevo; prikaže sporočilo, da se mora uporabiti POST za registracijo.
        response.setContentType("text/plain");
        response.getWriter().println("Prosimo, uporabite POST metodo za registracijo.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Pridobi podatke iz obrazca
        String ime = request.getParameter("ime");
        String email = request.getParameter("email");
        String geslo = request.getParameter("geslo");

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        // Preverjanje, če so vnešeni podatki pravilni
        if (ime == null || ime.isEmpty() || email == null || email.isEmpty() || geslo == null || geslo.isEmpty()) {
            out.print("⛔ Napaka: Vsa polja morajo biti izpolnjena!");
            return;
        }

        try {
            boolean uspeh = UporabnikDAO.registrirajUporabnika(ime, email, geslo);
            if (uspeh) {
                out.print("✅ Registracija uspešna!");
            } else {
                out.print("⛔ Napaka: E-pošta že obstaja!");
            }
        } catch (Exception e) {  // Ujame vse napake, vključno s SQLException
            out.print("⛔ Napaka pri registraciji: " + e.getMessage());
        }
    }
}
