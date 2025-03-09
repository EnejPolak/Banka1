package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/dobiTransakcije")
public class DobiTransakcijeServlet extends HttpServlet {

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

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Pridobi transakcije samo za prijavljenega uporabnika
        List<Transakcija> transakcije = TransakcijeDAO.getAllTransactionsForUser(uporabnikId);

        JSONArray jsonArray = new JSONArray();
        for (Transakcija t : transakcije) {
            JSONObject obj = new JSONObject();
            obj.put("id", t.getId());
            obj.put("tip", t.getTip());
            obj.put("znesek", t.getZnesek());
            obj.put("opis", t.getOpis());
            obj.put("kategorija", t.getKategorija());
            obj.put("datum", t.getDatumTransakcije().toString());
            jsonArray.put(obj);
        }
        out.print(jsonArray.toString());
        out.flush();
    }
}

