import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/testdb")
public class TestDBServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // Début du HTML avec Bootstrap inclus
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Test Database Connection</title>");
            out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container mt-5'>");
            out.println("<h1 class='text-center mb-4'>Test Database Connection</h1>");

            // Tentative de connexion à la base de données
            try (Connection connection = dataSource.getConnection()) {
                // Si la connexion réussit, afficher un message de succès
                out.println("<div class='alert alert-success text-center'>Connexion réussie à la base de données !</div>");

                // Interrogation de la table client
                String query = "SELECT Id_Client, nom, prenom, adresse, phone, password FROM client";


                try (PreparedStatement stmt = connection.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {

                    // Affichage des données de la table client
                    out.println("<h2 class='text-center mt-4'>Données des clients :</h2>");
                    out.println("<table class='table table-striped table-bordered mt-3'>");
                    out.println("<thead class='table-dark'>");
                    out.println("<tr>");
                    out.println("<th>Nom</th>");
                    out.println("<th>Prénom</th>");
                    out.println("<th>Adresse</th>");
                    out.println("<th>Téléphone</th>");
                    out.println("<th>Mot de Passe</th>");
                    out.println("<th>Actions</th>");
                    out.println("</tr>");
                    out.println("</thead>");
                    out.println("<tbody>");

                    while (rs.next()) {
                        int Id_Client = rs.getInt("Id_Client");
                        String nom = rs.getString("nom");
                        String prenom = rs.getString("prenom");
                        String adresse = rs.getString("adresse");
                        String phone = rs.getString("phone");
                        String password = rs.getString("password");

                        out.println("<tr>");
                        out.println("<td>" + nom + "</td>");
                        out.println("<td>" + prenom + "</td>");
                        out.println("<td>" + adresse + "</td>");
                        out.println("<td>" + phone + "</td>");
                        out.println("<td>" + password + "</td>");
                        out.println("<td>");
                        // Bouton Modifier
                        out.println("<form action='editClient' method='get' style='display:inline;'>");
                        out.println("<input type='hidden' name='Id_Client' value='" + Id_Client + "'>");
                        out.println("<button type='submit' class='btn btn-primary btn-sm'>Modifier</button>");
                        out.println("</form>");
                        // Bouton Supprimer
                        out.println("<form action='deleteClient' method='post' style='display:inline;'>");
                        out.println("<input type='hidden' name='Id_Client' value='" + Id_Client + "'>");
                        out.println("<button type='submit' class='btn btn-danger btn-sm'>Supprimer</button>");
                        out.println("</form>");
                        out.println("</td>");
                        out.println("</tr>");
                    }

                    out.println("</tbody>");
                    out.println("</table>");
                }
            } catch (Exception e) {
                // Afficher un message d'erreur en cas de problème
                out.println("<div class='alert alert-danger text-center'>Erreur lors de la connexion à la base de données : " + e.getMessage() + "</div>");
                e.printStackTrace(out);
            }

            out.println("</div>"); // Fin du container
            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
