

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

@WebServlet("/insertClient")
public class InsertClientServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS")
    private DataSource dataSource;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Récupération des paramètres du formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String adresse = request.getParameter("adresse");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        // Validation de base des champs (facultatif mais conseillé)
        if (nom == null || prenom == null || adresse == null || phone == null || password == null ||
                nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tous les champs sont obligatoires.");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO client (nom, prenom, adresse, phone, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nom);
                statement.setString(2, prenom);
                statement.setString(3, adresse);
                statement.setString(4, phone);
                statement.setString(5, password);

                statement.executeUpdate();
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'insertion du client", e);
        }

        // Redirection après succès
        response.sendRedirect("login&client.jsp");
    }
}
