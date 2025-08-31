package com.example.restau.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.example.restau.Model.Menu;

@WebServlet("/Menu") // Mapping de la servlet
public class MenuServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS") // Assurez-vous que cette ressource est configurée dans votre serveur (e.g., context.xml de Tomcat)
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Menu> menus = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            // Requête SQL pour récupérer les menus
            String query = "SELECT * FROM menu";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                // Parcours des résultats
                while (rs.next()) {
                    // Gestion des valeurs NULL ou incorrectes
                    int quantity = rs.getObject("quantity") != null ? rs.getInt("quantity") : 0;

                    double price = 0;
                    if (rs.getString("prix") != null) {
                        String prixString = rs.getString("prix").replace(" MAD", "").trim();
                        price = Double.parseDouble(prixString);
                    }

                    // Création de l'objet Menu
                    Menu menu = new Menu(
                            rs.getInt("Id_Menu"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("categorie"),
                            quantity,
                            price
                    );
                    menus.add(menu);
                }
            }
        } catch (Exception e) {
            // Gestion des erreurs SQL ou de connexion
            e.printStackTrace();
            throw new ServletException("Erreur lors de la récupération des menus : " + e.getMessage(), e);
        }

        // Logs pour vérifier les données
        System.out.println("Nombre de menus récupérés : " + menus.size());

        // Transmission des menus à la JSP
        request.setAttribute("menus", menus);

        // Redirection vers la vue (JSP)
        request.getRequestDispatcher("/WEB-INF/menu.jsp").forward(request, response);
    }
}
