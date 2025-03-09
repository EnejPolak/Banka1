package com.jetbrains.hellowebapp;

public class Kraj {
    private int id;
    private String ime;
    private int postnaSt;

    public Kraj(int id, String ime, int postnaSt) {
        this.id = id;
        this.ime = ime;
        this.postnaSt = postnaSt;
    }

    public int getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public int getPostnaSt() {
        return postnaSt;
    }
}
