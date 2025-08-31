

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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS")
    private DataSource dataSource;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Récupération des paramètres du formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String password = request.getParameter("password");

        // Validation de base
        if (nom == null || prenom == null || password == null ||
                nom.isEmpty() || prenom.isEmpty() || password.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tous les champs sont obligatoires.");
            return;
        }

        boolean isAuthenticated = false;

        // Vérification des identifiants
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM client WHERE nom = ? AND prenom = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nom);
                statement.setString(2, prenom);
                statement.setString(3, password);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isAuthenticated = true;
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la vérification des identifiants", e);
        }

        // Redirection en fonction du résultat
        if (isAuthenticated) {
            response.sendRedirect("restaurants"); // Page en cas de succès
        } else {
            response.sendRedirect("login.html?error=true"); // Page en cas d'échec
        }
    }
}
