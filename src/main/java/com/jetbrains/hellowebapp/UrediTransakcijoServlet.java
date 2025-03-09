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
import java.time.LocalDate;

@WebServlet("/urediTransakcijo")
public class UrediTransakcijoServlet extends HttpServlet {

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

        String idStr = request.getParameter("id");
        String znesekStr = request.getParameter("znesek");
        String opis = request.getParameter("opis");
        String datumStr = request.getParameter("datum");
        String kategorijaStr = request.getParameter("kategorija");

        if (idStr == null || znesekStr == null || datumStr == null || kategorijaStr == null) {
            out.print("⛔ Manjkajo parametri (id, znesek, datum ali kategorija).");
            return;
        }

        int id;
        BigDecimal znesek;
        try {
            id = Integer.parseInt(idStr);
            znesek = new BigDecimal(znesekStr);
        } catch (NumberFormatException e) {
            out.print("⛔ Neveljaven ID ali znesek.");
            return;
        }

        LocalDate datum;
        try {
            datum = LocalDate.parse(datumStr);
        } catch (Exception e) {
            out.print("⛔ Neveljaven datum.");
            return;
        }

        String tip = request.getParameter("tip");
        int kategorijaId;
        if ("addNew".equals(kategorijaStr)) {
            String novaKategorija = request.getParameter("novaKategorija");
            if (novaKategorija == null || novaKategorija.trim().isEmpty()) {
                out.print("⛔ Vnesite ime nove kategorije.");
                return;
            }
            kategorijaId = KategorijeDAO.dobiAliNarediKategorijo(uporabnikId, novaKategorija, tip);
            if (kategorijaId == -1) {
                out.print("⛔ Napaka pri vstavljanju nove kategorije.");
                return;
            }
        } else {
            kategorijaId = KategorijeDAO.getKategorijaIdByName(kategorijaStr);
            if (kategorijaId == -1) {
                out.print("⛔ Neveljavna kategorija.");
                return;
            }
        }

        boolean uspeh = TransakcijeDAO.posodobiTransakcijo(id, znesek, opis, datum, kategorijaId);
        if (uspeh) {
            out.print("✅ Transakcija uspešno posodobljena.");
        } else {
            out.print("⛔ Napaka pri posodabljanju transakcije.");
        }
    }
}

