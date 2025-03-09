package com.jetbrains.hellowebapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransakcijeDAO {

    public static List<Transakcija> getAllTransactionsForUser(int uporabnikId) {
        List<Transakcija> list = new ArrayList<>();
        String query = "SELECT t.id, t.tip, t.znesek, t.opis, k.ime AS kategorija, t.datum_transakcije " +
                "FROM transakcije t " +
                "LEFT JOIN kategorije k ON t.kategorija_id = k.id " +
                "WHERE t.uporabnik_id = ? " +
                "ORDER BY EXTRACT(YEAR FROM t.datum_transakcije) DESC, t.datum_transakcije DESC";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, uporabnikId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String tip = rs.getString("tip");
                BigDecimal znesek = rs.getBigDecimal("znesek");
                String opis = rs.getString("opis");
                String kategorija = rs.getString("kategorija");
                LocalDate datum = rs.getDate("datum_transakcije").toLocalDate();
                list.add(new Transakcija(id, tip, znesek, opis, kategorija, datum));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean izbrisiTransakcijo(int id) {
        String query = "DELETE FROM transakcije WHERE id = ?";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean posodobiTransakcijo(int id, BigDecimal znesek, String opis, LocalDate datum, int kategorijaId) {
        String sql = "UPDATE transakcije SET znesek = ?, opis = ?, datum_transakcije = ?, kategorija_id = ? WHERE id = ?";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, znesek);
            stmt.setString(2, opis);
            stmt.setDate(3, java.sql.Date.valueOf(datum));
            stmt.setInt(4, kategorijaId);
            stmt.setInt(5, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
