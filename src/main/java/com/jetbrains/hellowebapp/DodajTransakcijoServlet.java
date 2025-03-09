package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/dodajTransakcijo")
public class DodajTransakcijoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Preverjanje seje
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("uporabnikId") == null) {
            response.sendRedirect("Prijava.html");
            return;
        }
        int uporabnikId = (int) session.getAttribute("uporabnikId");

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String kategorija = request.getParameter("kategorija");
        String tip = request.getParameter("tip");
        String znesekStr = request.getParameter("znesek");
        String datumStr = request.getParameter("datum_transakcije");
        String opis = request.getParameter("opis");

        if (kategorija == null || kategorija.trim().isEmpty() ||
                tip == null || tip.trim().isEmpty() ||
                znesekStr == null || znesekStr.trim().isEmpty() ||
                datumStr == null || datumStr.trim().isEmpty()) {
            out.println("⛔ Napaka: Manjkajo potrebni podatki.");
            return;
        }

        BigDecimal znesek;
        try {
            znesek = new BigDecimal(znesekStr);
            if (znesek.compareTo(BigDecimal.ZERO) <= 0) {
                out.println("⛔ Napaka: Znesek mora biti večji od 0.");
                return;
            }
        } catch (NumberFormatException e) {
            out.println("⛔ Napaka: Neveljaven znesek.");
            return;
        }

        LocalDate datumTransakcije;
        try {
            datumTransakcije = LocalDate.parse(datumStr);
        } catch (Exception e) {
            out.println("⛔ Napaka: Neveljaven datum.");
            return;
        }

        // Popravljen klic: uporabimo dobiAliNarediKategorijo namesto getOrCreateCategory
        int kategorijaId = KategorijeDAO.dobiAliNarediKategorijo(uporabnikId, kategorija.trim(), tip.trim());
        if (kategorijaId == -1) {
            out.println("⛔ Napaka: Kategorija ni bila ustvarjena.");
            return;
        }

        String sql = "INSERT INTO transakcije (uporabnik_id, kategorija_id, znesek, tip, opis, datum_transakcije) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, uporabnikId);
            stmt.setInt(2, kategorijaId);
            stmt.setBigDecimal(3, znesek);
            stmt.setString(4, tip);
            stmt.setString(5, opis);
            stmt.setDate(6, java.sql.Date.valueOf(datumTransakcije));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                out.println("✅ Transakcija uspešno dodana!");
            } else {
                out.println("⛔ Napaka: Transakcija ni bila dodana.");
            }
        } catch (SQLException e) {
            out.println("⛔ Napaka pri dodajanju transakcije: " + e.getMessage());
        }
    }
}
