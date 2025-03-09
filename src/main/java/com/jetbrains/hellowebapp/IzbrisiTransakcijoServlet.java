package com.jetbrains.hellowebapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/izbrisiTransakcijo")
public class IzbrisiTransakcijoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (idStr == null || idStr.trim().isEmpty()) {
            out.print("⛔ Napaka: Manjkajoči ID transakcije.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            out.print("⛔ Napaka: Neveljaven ID.");
            return;
        }

        boolean uspeh = TransakcijeDAO.izbrisiTransakcijo(id);
        if (uspeh) {
            out.print("✅ Transakcija uspešno izbrisana.");
        } else {
            out.print("⛔ Napaka pri brisanju transakcije.");
        }
    }
}
