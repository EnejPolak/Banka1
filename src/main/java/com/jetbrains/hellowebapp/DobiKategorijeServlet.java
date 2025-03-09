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

@WebServlet("/dobiKategorije")
public class DobiKategorijeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Preverjanje seje
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("uporabnikId") == null) {
            response.sendRedirect("Prijava.html");
            return;
        }
        int uporabnikId = (int) session.getAttribute("uporabnikId");

        // Pridobi kategorije samo za prijavljenega uporabnika
        List<Kategorija> kategorije = KategorijeDAO.dobiKategorijeZaUporabnika(uporabnikId);

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        JSONArray jsonArray = new JSONArray();
        for (Kategorija kat : kategorije) {
            JSONObject obj = new JSONObject();
            obj.put("id", kat.getId());
            obj.put("ime", kat.getIme());
            obj.put("tip", kat.getTip());
            jsonArray.put(obj);
        }
        out.print(jsonArray.toString());
        out.flush();
    }
}

