package com.jetbrains.hellowebapp;

public class Kategorija {
    private int id;
    private String ime;
    private String tip;

    public Kategorija(int id, String ime, String tip) {
        this.id = id;
        this.ime = ime;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public String getTip() {
        return tip;
    }
}
