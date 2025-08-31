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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/table-reservation")
public class TableReservationServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS")
    private DataSource dataSource;

    // Structure pour les informations des tables
    public static class Table {
        private int id;
        private String number;
        private int capacity;

        public Table(int id, String number, int capacity) {
            this.id = id;
            this.number = number;
            this.capacity = capacity;
        }

        public int getId() { return id; }
        public String getNumber() { return number; }
        public int getCapacity() { return capacity; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Table> availableTables = new ArrayList<>();

        // Récupération des tables disponibles
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT Id_table, Number, Capacity FROM `table` WHERE Status = 'Disponible'";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("Id_table");
                    String number = resultSet.getString("Number");
                    int capacity = resultSet.getInt("Capacity");
                    availableTables.add(new Table(id, number, capacity));
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la récupération des tables disponibles", e);
        }

        // Affichage des tables disponibles dans le HTML
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head><title>Tables Disponibles</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("</head><body >");

            // Navbar Bootstrap
            out.println("<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>");
            out.println("<div class='container'>");
            out.println("<a class='navbar-brand' href='#'>MyApp</a>");
            out.println("<button class='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav' aria-controls='navbarNav' aria-expanded='false' aria-label='Toggle navigation'>");
            out.println("<span class='navbar-toggler-icon'></span>");
            out.println("</button>");
            out.println("<div class='collapse navbar-collapse' id='navbarNav'>");
            out.println("<ul class='navbar-nav ms-auto'>");
            out.println("<li class='nav-item'><a class='nav-link' href='#'>Accueil</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='reserved-tables'>tables reserved</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='#'>Contact</a></li>");
            out.println("</ul>");
            out.println("</div>");
            out.println("</div>");
            out.println("</nav>");

            // Contenu principal
            out.println("<div class='container mt-5'>");
            out.println("<h2 class='mb-4'>Tables Disponibles</h2>");

            // Message de succès si réservation réussie
            String success = request.getParameter("success");
            if ("true".equals(success)) {
                out.println("<div class='alert alert-success'>Table réservée avec succès !</div>");
            }

            // Tableau des tables disponibles
            out.println("<table class='table table-bordered table-striped'>");
            out.println("<thead><tr><th>#</th><th>Numéro</th><th>Capacité</th><th>Action</th></tr></thead>");
            out.println("<tbody>");
            for (Table table : availableTables) {
                out.println("<tr>");
                out.println("<td>" + table.getId() + "</td>");
                out.println("<td>" + table.getNumber() + "</td>");
                out.println("<td>" + table.getCapacity() + " personnes</td>");
                out.println("<td>");
                // Lien vers le servlet de réservation avec l'id de la table
                out.println("<a href='menu"  + "' class='btn btn-primary'>Réserver</a>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");



            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tableId = request.getParameter("table_id");

        // Mise à jour du statut de la table à 'Réservée'
        try (Connection connection = dataSource.getConnection()) {
            String sql = "UPDATE `table` SET Status = 'Réservée' WHERE Id_table = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, Integer.parseInt(tableId));
                statement.executeUpdate();
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la réservation de la table", e);
        }

        // Rediriger vers la même page avec un paramètre de succès
        response.sendRedirect("table-reservation?success=true");
    }
}
