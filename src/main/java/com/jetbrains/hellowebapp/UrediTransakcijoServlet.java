package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet("/urediTransakcijo")
public class UrediTransakcijoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Preberi parametre
        String idStr = request.getParameter("id");
        String znesekStr = request.getParameter("znesek");
        String opis = request.getParameter("opis");
        String datumStr = request.getParameter("datum");       // nov parameter
        String kategorijaStr = request.getParameter("kategorija"); // nov parameter

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

        // Pretvori datum iz Stringa v LocalDate
        LocalDate datum;
        try {
            datum = LocalDate.parse(datumStr);
        } catch (Exception e) {
            out.print("⛔ Neveljaven datum.");
            return;
        }

        // Preberi parameter tip (npr. "prihodek" ali "odhodek")
        String tip = request.getParameter("tip");

        int kategorijaId;
        if ("addNew".equals(kategorijaStr)) {
            // Če uporabnik izbere "Dodaj novo kategorijo", preberi novo ime kategorije
            String novaKategorija = request.getParameter("novaKategorija");
            if (novaKategorija == null || novaKategorija.trim().isEmpty()) {
                out.print("⛔ Vnesite ime nove kategorije.");
                return;
            }
            // Pridobi uporabniški ID iz seje (predpostavljamo, da je shranjen v seji)
            int uporabnikId = (int) request.getSession().getAttribute("uporabnikId");
            // Uporabi metodo, ki preveri obstoj in sicer ustvari novo kategorijo
            kategorijaId = KategorijeDAO.getOrCreateCategory(uporabnikId, novaKategorija, tip);
            if (kategorijaId == -1) {
                out.print("⛔ Napaka pri vstavljanju nove kategorije.");
                return;
            }
        } else {
            // Če ni izbrana možnost "addNew", pridobi ID kategorije glede na ime
            kategorijaId = KategorijeDAO.getKategorijaIdByName(kategorijaStr);
            if (kategorijaId == -1) {
                out.print("⛔ Neveljavna kategorija.");
                return;
            }
        }

        // Pokliči metodo za posodobitev transakcije v DAO-ju
        boolean uspeh = TransakcijeDAO.posodobiTransakcijo(id, znesek, opis, datum, kategorijaId);
        if (uspeh) {
            out.print("✅ Transakcija uspešno posodobljena.");
        } else {
            out.print("⛔ Napaka pri posodabljanju transakcije.");
        }
    }
}
