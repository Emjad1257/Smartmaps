package com.example.restau.controller;

import com.example.restau.Model.PanierItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/RemoveFromCart")
public class RemoveFromCartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idMenu = Integer.parseInt(request.getParameter("idMenu"));

        HttpSession session = request.getSession();
        List<PanierItem> panier = (List<PanierItem>) session.getAttribute("panier");

        if (panier != null) {
            panier.removeIf(item -> item.getIdMenu() == idMenu);
            session.setAttribute("panier", panier);
        }

        response.sendRedirect("Menu");
    }
}
