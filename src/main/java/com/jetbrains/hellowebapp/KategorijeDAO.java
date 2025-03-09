package com.jetbrains.hellowebapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategorijeDAO {

    // Preveri, če kategorija obstaja; če ne, jo ustvari in vrne njen ID
    public static int getOrCreateCategory(int uporabnikId, String ime, String tip) {
        String query = "SELECT id FROM kategorije WHERE uporabnik_id = ? AND ime = ? AND tip = ?";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, uporabnikId);
            stmt.setString(2, ime);
            stmt.setString(3, tip);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Če kategorije ni, jo ustvarimo
        String insert = "INSERT INTO kategorije (uporabnik_id, ime, tip) VALUES (?, ?, ?)";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, uporabnikId);
            stmt.setString(2, ime);
            stmt.setString(3, tip);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static int getKategorijaIdByName(String imeKategorije) {
        String sql = "SELECT id FROM kategorije WHERE ime = ?";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imeKategorije);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // vrnemo -1, če kategorije ne najdemo
    }

    // Vrne seznam kategorij za določenega uporabnika
    public static List<Kategorija> dobiKategorijeZaUporabnika(int uporabnikId) {
        List<Kategorija> seznam = new ArrayList<>();
        String query = "SELECT id, ime, tip FROM kategorije WHERE uporabnik_id = ?";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, uporabnikId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String ime = rs.getString("ime");
                String tip = rs.getString("tip");
                seznam.add(new Kategorija(id, ime, tip));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seznam;
    }
}
