package com.jetbrains.hellowebapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Razred za delo s kategorijami preko strežniških podprogramov.
 */
public class KategorijeDAO {

    /**
     * Preveri, ali kategorija z določenim imenom in tipom obstaja za danega uporabnika.
     * Če kategorija ne obstaja, jo ustvari in vrne njen ID.
     *
     * @param uporabnikId ID uporabnika
     * @param ime         Ime kategorije
     * @param tip         Tip kategorije
     * @return ID kategorije ali -1, če pride do napake
     */
    public static int dobiAliNarediKategorijo(int uporabnikId, String ime, String tip) {
        String callSQL = "{ ? = call sp_dobi_ali_naredi_kategorijo(?, ?, ?) }";
        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            // Registriramo prvi parameter kot izhodni (tip INTEGER)
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setInt(2, uporabnikId);
            cstmt.setString(3, ime);
            cstmt.setString(4, tip);

            cstmt.execute();
            return cstmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Vrne ID kategorije na podlagi imena.
     *
     * @param imeKategorije Ime kategorije
     * @return ID kategorije ali -1, če kategorija ne obstaja
     */
    public static int getKategorijaIdByName(String imeKategorije) {
        String callSQL = "{ ? = call sp_get_kategorija_id_by_name(?) }";
        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setString(2, imeKategorije);

            cstmt.execute();
            return cstmt.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Vrne seznam vseh kategorij, ki pripadajo določenemu uporabniku.
     *
     * @param uporabnikId ID uporabnika
     * @return Seznam objektov tipa Kategorija
     */
    public static List<Kategorija> dobiKategorijeZaUporabnika(int uporabnikId) {
        List<Kategorija> seznam = new ArrayList<>();
        String sql = "SELECT * FROM sp_dobi_kategorije_za_uporabnika(?)";
        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, uporabnikId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String ime = rs.getString("ime");
                    String tip = rs.getString("tip");
                    seznam.add(new Kategorija(id, ime, tip));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seznam;
    }
}
