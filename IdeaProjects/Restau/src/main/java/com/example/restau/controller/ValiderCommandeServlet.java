package com.example.restau.controller;

import com.example.restau.Model.PanierItem;
import com.example.restau.Model.Serveur;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ValiderCommandeServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<PanierItem> panier = (List<PanierItem>) session.getAttribute("panier");

        if (panier == null || panier.isEmpty()) {
            response.sendRedirect("menu?error=panierVide");
            return;
        }

        List<Serveur> serveurs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            // Récupérer les serveurs
            String queryServeurs = "SELECT id_serveurs, nom FROM serveurs";
            try (PreparedStatement stmt = connection.prepareStatement(queryServeurs);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    serveurs.add(new Serveur(rs.getInt("id_serveurs"), rs.getString("nom")));
                }
            }
            // Ajouter les serveurs en attribut pour JSP
            request.setAttribute("serveurs", serveurs);
            request.setAttribute("panier", panier);
            request.getRequestDispatcher("/WEB-INF/ValiderCommande.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Erreur lors de la récupération des serveurs.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<PanierItem> panier = (List<PanierItem>) session.getAttribute("panier");

        if (panier == null || panier.isEmpty()) {
            response.sendRedirect("menu?error=panierVide");
            return;
        }

        int idServeur = Integer.parseInt(request.getParameter("idServeur")); // Récupération du serveur sélectionné

        try (Connection connection = dataSource.getConnection()) {
            String insertCommande = "INSERT INTO commande (date_order, status, total_prix, id_client, id_serveurs, Id_Menu) VALUES (NOW(), ?, ?, ?, ?, ?)";

            for (PanierItem item : panier) {
                try (PreparedStatement stmt = connection.prepareStatement(insertCommande)) {
                    stmt.setString(1, "En Cours");
                    stmt.setDouble(2, item.getTotalPrice());
                    stmt.setInt(3, 1); // ID client statique
                    stmt.setInt(4, idServeur); // ID serveur sélectionné
                    stmt.setInt(5, item.getIdMenu());
                    stmt.executeUpdate();
                }
            }

            session.removeAttribute("panier");
            response.sendRedirect("menu?success=commandeValidee");
        } catch (SQLException e) {
            throw new ServletException("Erreur lors de la validation de la commande.", e);
 }
}
}