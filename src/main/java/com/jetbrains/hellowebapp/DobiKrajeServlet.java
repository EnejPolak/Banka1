package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/dobiKraje")
public class DobiKrajeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Nastavimo vrsto vsebine na JSON, z UTF-8 kodiranjem
        response.setContentType("application/json; charset=UTF-8");

        // Pridobimo seznam krajev iz DAO
        List<Kraj> kraji = KrajiDAO.dobiKraje();

        // Sestavimo JSON array iz seznama krajev
        JSONArray jsonArray = new JSONArray();
        for (Kraj k : kraji) {
            JSONObject obj = new JSONObject();
            obj.put("id", k.getId());
            obj.put("ime", k.getIme());
            obj.put("postna_st", k.getPostnaSt());
            jsonArray.put(obj);
        }

        // Zapišemo JSON array v odziv
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonArray.toString());
        }
    }
}
