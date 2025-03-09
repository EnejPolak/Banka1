package com.jetbrains.hellowebapp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;


public class TransakcijeDAO {

    private static final Logger logger = Logger.getLogger(TransakcijeDAO.class.getName());

    /**
     * Vrne seznam transakcij za določenega uporabnika.
     */
    public static List<Transakcija> getAllTransactionsForUser(int uporabnikId) {
        List<Transakcija> list = new ArrayList<>();
        String sql = "SELECT * FROM sp_dobi_transakcije(?)";

        try (Connection conn = PovezavaZBazo.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, uporabnikId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tip = rs.getString("tip");
                    BigDecimal znesek = rs.getBigDecimal("znesek");
                    String opis = rs.getString("opis");
                    String kategorija = rs.getString("kategorija");
                    LocalDate datum = rs.getDate("datum_transakcije").toLocalDate();
                    list.add(new Transakcija(id, tip, znesek, opis, kategorija, datum));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Dodajanje transakcije s funkcijo sp_dodaj_in_log_transakcijo.
     */
    public static boolean dodajTransakcijo(int uporabnikId, int kategorijaId, BigDecimal znesek, String tip, String opis, LocalDate datumTransakcije) {
        String callSQL = "{ ? = call sp_dodaj_in_log_transakcijo(?, ?, ?, ?, ?, ?) }";
        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.setInt(2, uporabnikId);
            cstmt.setInt(3, kategorijaId);
            cstmt.setBigDecimal(4, znesek);
            cstmt.setString(5, tip);
            cstmt.setString(6, opis);
            cstmt.setDate(7, java.sql.Date.valueOf(datumTransakcije));

            cstmt.execute();
            boolean result = cstmt.getBoolean(1);
            if (result) {
                logger.log(Level.INFO, "Transakcija uspešno dodana in logirana za uporabnika {0}.", uporabnikId);
            } else {
                logger.log(Level.WARNING, "Prišlo je do napake pri dodajanju transakcije za uporabnika {0}.", uporabnikId);
            }
            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL napaka pri dodajanju transakcije: {0}", e.getMessage());
            return false;
        }
    }



    /**
     * Posodobi obstoječo transakcijo preko stored procedure sp_posodobi_transakcijo.
     */
    public static boolean posodobiTransakcijo(int id, BigDecimal znesek, String opis, LocalDate datum, int kategorijaId) {
        String callSQL = "{ ? = call sp_posodobi_transakcijo(?, ?, ?, ?, ?) }";
        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.setInt(2, id);
            cstmt.setBigDecimal(3, znesek);
            cstmt.setString(4, opis);
            cstmt.setDate(5, java.sql.Date.valueOf(datum));
            cstmt.setInt(6, kategorijaId);

            cstmt.execute();
            boolean rezultat = cstmt.getBoolean(1);
            System.out.println("Rezultat procedure: " + rezultat);
            return cstmt.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Izbriše transakcijo preko stored procedure sp_izbrisi_transakcijo.
     * Vrne true, če je transakcija uspešno izbrisana.
     *
     * @param id ID transakcije
     * @return true, če izbrisana, sicer false
     */
    public static boolean izbrisiTransakcijo(int id) {
        String callSQL = "{ ? = call sp_izbrisi_transakcijo(?) }";
        try (Connection conn = PovezavaZBazo.connect();
             CallableStatement cstmt = conn.prepareCall(callSQL)) {

            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.setInt(2, id);

            cstmt.execute();
            return cstmt.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
