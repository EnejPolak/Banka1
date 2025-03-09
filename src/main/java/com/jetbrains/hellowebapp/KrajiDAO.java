package com.jetbrains.hellowebapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KrajiDAO {
    public static List<Kraj> dobiKraje() {
        List<Kraj> seznam = new ArrayList<>();
        String sql = "SELECT * FROM sp_dobi_kraje()";  // Ta funkcija mora v bazi vrniti stolpce: id, ime, postna_st
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()){
                int id = rs.getInt("id");
                String ime = rs.getString("ime");
                int postnaSt = rs.getInt("postna_st");
                seznam.add(new Kraj(id, ime, postnaSt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seznam;
    }
}
