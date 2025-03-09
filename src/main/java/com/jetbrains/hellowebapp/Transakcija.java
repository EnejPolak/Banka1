package com.jetbrains.hellowebapp;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transakcija {
    private int id;
    private String tip;
    private BigDecimal znesek;
    private String opis;
    private String kategorija;
    private LocalDate datumTransakcije;

    public Transakcija(int id, String tip, BigDecimal znesek, String opis, String kategorija, LocalDate datumTransakcije) {
        this.id = id;
        this.tip = tip;
        this.znesek = znesek;
        this.opis = opis;
        this.kategorija = kategorija;
        this.datumTransakcije = datumTransakcije;
    }

    public int getId() { return id; }
    public String getTip() { return tip; }
    public BigDecimal getZnesek() { return znesek; }
    public String getOpis() { return opis; }
    public String getKategorija() { return kategorija; }
    public LocalDate getDatumTransakcije() { return datumTransakcije; }
}
