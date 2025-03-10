package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/izpisiPdf")
public class PdfIzpisServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Preverjanje, če je uporabnik prijavljen
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("uporabnikId") == null) {
            response.sendRedirect("Prijava.html");
            return;
        }
        int uporabnikId = (int) session.getAttribute("uporabnikId");

        // Pridobi ime uporabnika preko podprograma
        String username = getUsername(uporabnikId);
        if (username.isEmpty()) {
            response.getWriter().println("⛔ Napaka pri pridobivanju uporabniškega imena.");
            return;
        }

        // Določimo časovno obdobje: zadnjih 30 dni
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        // Pridobi transakcije preko strežniškega podprograma sp_dobi_transakcije,
        // nato jih na ravni SQL filtriramo za zadnjih 30 dni
        List<Transakcija> transakcije = new ArrayList<>();
        String query = "SELECT * FROM sp_dobi_transakcije(?) t WHERE t.datum_transakcije >= ? ORDER BY t.datum_transakcije DESC";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, uporabnikId);
            ps.setDate(2, java.sql.Date.valueOf(thirtyDaysAgo));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tip = rs.getString("tip");
                    BigDecimal znesek = rs.getBigDecimal("znesek");
                    String opis = rs.getString("opis");
                    String kategorija = rs.getString("kategorija");
                    LocalDate datum = rs.getDate("datum_transakcije").toLocalDate();
                    transakcije.add(new Transakcija(id, tip, znesek, opis, kategorija, datum));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("⛔ Napaka pri pridobivanju transakcij.");
            return;
        }

        // Izračun skupnih vsot prihodkov in odhodkov
        BigDecimal totalPrihodki = BigDecimal.ZERO;
        BigDecimal totalOdhodki = BigDecimal.ZERO;
        for (Transakcija t : transakcije) {
            if ("prihodek".equalsIgnoreCase(t.getTip())) {
                totalPrihodki = totalPrihodki.add(t.getZnesek());
            } else if ("odhodek".equalsIgnoreCase(t.getTip())) {
                totalOdhodki = totalOdhodki.add(t.getZnesek());
            }
        }

        // Sestavi ime PDF datoteke
        String fileName = username + "-Izps-" + totalPrihodki + "-" + totalOdhodki + "-" + today.toString() + ".pdf";

        // Ustvari PDF z uporabo Apache PDFBox
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Naslov PDF-ja
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Tranzakcije zadnjih 30 dni");
            contentStream.endText();

            // Izpis transakcij (osnovni izpis, ena vrstica na transakcijo)
            int yPosition = 720;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            for (Transakcija t : transakcije) {
                if (yPosition < 50) { // Če zmanjka prostora, lahko dodaš novo stran (tu pa prekinemo)
                    break;
                }
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                String vrstica = t.getDatumTransakcije() + " | " + t.getTip() + " | "
                        + t.getZnesek() + " | " + t.getOpis() + " | " + t.getKategorija();
                contentStream.showText(vrstica);
                contentStream.endText();
                yPosition -= 20;
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.getWriter().println("⛔ Napaka pri generiranju PDF-ja.");
            return;
        }

        // Nastavi glavo odgovora in pošlji PDF datoteko kot prenos
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        document.save(response.getOutputStream());
        document.close();
    }

    // Podprogram za pridobitev imena uporabnika glede na ID
    private String getUsername(int uporabnikId) {
        String username = "";
        String sql = "SELECT ime FROM uporabniki WHERE id = ?";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, uporabnikId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("ime");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
}
