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

@WebServlet("/dobiKategorije")
public class DobiKategorijeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // V praksi dobiš uporabnikId iz seje, tu ga za primer postavimo na 1
        int uporabnikId = 1;
        // Dobimo seznam kategorij iz KategorijeDAO
        List<Kategorija> kategorije = KategorijeDAO.dobiKategorijeZaUporabnika(uporabnikId);

        // Nastavimo MIME tip na JSON
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Sestavimo JSON array
        JSONArray jsonArray = new JSONArray();
        for (Kategorija kat : kategorije) {
            JSONObject obj = new JSONObject();
            obj.put("id", kat.getId());
            obj.put("ime", kat.getIme());
            obj.put("tip", kat.getTip());
            jsonArray.put(obj);
        }

        // Vrnemo JSON
        out.print(jsonArray.toString());
        out.flush();
    }
}
