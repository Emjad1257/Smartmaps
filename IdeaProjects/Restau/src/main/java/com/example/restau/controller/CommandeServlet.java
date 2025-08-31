package com.example.restau.controller;

import com.example.restau.Model.PanierItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Récupérer les paramètres
            String idMenuParam = request.getParameter("idMenu");
            String title = request.getParameter("title");
            String prixParam = request.getParameter("prix");
            String quantityParam = request.getParameter("quantity");

            // Vérifier que les paramètres sont présents
            if (idMenuParam == null || title == null || prixParam == null || quantityParam == null) {
                throw new IllegalArgumentException("Un ou plusieurs paramètres sont manquants.");
            }

            // Convertir les paramètres
            int idMenu = Integer.parseInt(idMenuParam);
            double prix = Double.parseDouble(prixParam);
            int quantity = Integer.parseInt(quantityParam);

            // Vérification des valeurs (ex : quantité non négative)
            if (quantity <= 0 || prix <= 0) {
                throw new IllegalArgumentException("La quantité et le prix doivent être positifs.");
            }

            // Créer un objet PanierItem
            PanierItem item = new PanierItem(idMenu, title, quantity, prix * quantity);

            // Récupérer la session et ajouter l'article au panier
            HttpSession session = request.getSession();
            List<PanierItem> panier = (List<PanierItem>) session.getAttribute("panier");

            if (panier == null) {
                panier = new ArrayList<>();
            }

            panier.add(item);
            session.setAttribute("panier", panier);

            // Redirection vers la page menu.jsp
            response.sendRedirect("menu");
        } catch (NumberFormatException e) {
            // Gérer les erreurs de conversion (paramètres non numériques)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Les paramètres idMenu, prix ou quantity sont invalides.");
        } catch (IllegalArgumentException e) {
            // Gérer les erreurs de validation des données
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // Gérer toute autre exception
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Une erreur s'est produite lors du traitement de la commande.");
        }
    }
}
