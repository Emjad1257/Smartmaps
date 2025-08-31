package com.example.restau.controller;

import com.example.restau.Model.Serveur;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Serveurs") // Mapping pour accéder à la servlet via l'URL /Serveurs
public class ServeursServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS") // Nom de la ressource configurée dans le serveur
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Serveur> serveurs = new ArrayList<>();

        // Connexion à la base de données et récupération des données
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT id_serveurs, nom, numerotele, shift, id_restaurant FROM serveurs";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Serveur serveur = new Serveur(
                            rs.getInt("id_serveurs"),
                            rs.getString("nom"),
                            rs.getString("numerotele"),
                            rs.getString("shift"),
                            rs.getInt("id_restaurant")
                    );
                    serveurs.add(serveur);
                }
            }

        } catch (Exception e) {
            throw new ServletException("Erreur lors de la récupération des serveurs.", e);
        }

        // Ajouter les serveurs à l'attribut de requête
        request.setAttribute("serveurs", serveurs);

        // Redirection vers le JSP pour afficher les données
        request.getRequestDispatcher("/WEB-INF/Serveurs.jsp").forward(request, response);
    }
}
