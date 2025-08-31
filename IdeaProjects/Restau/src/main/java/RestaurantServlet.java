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

@WebServlet("/restaurants")
public class RestaurantServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS") // Pool de connexion configuré dans Tomcat
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            // Début du HTML
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Liste des Restaurants</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("</head>");
            out.println("<body class=\"d-flex flex-column min-vh-100\" style=\"\n" +
                    "    background-image: url('images/home.jpg');\n" +
                    "    background-size: cover;\n" +
                    "    background-repeat: no-repeat;\n" +
                    "    background-position: center;\">");

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
            out.println("<li class='nav-item'><a class='nav-link' href='#'>À propos</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='#'>Contact</a></li>");
            out.println("</ul>");
            out.println("</div>");
            out.println("</div>");
            out.println("</nav>");

            // Conteneur principal
            out.println("<div class='container mt-5'>");
            out.println("<div class='row'>");

            // Connexion et récupération des données
            try (Connection connection = dataSource.getConnection()) {
                String sql = "SELECT Id_Restaurant, nom, adresse, phone, email, opening_hours, description FROM restaurant";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        boolean hasData = false;
                        while (resultSet.next()) {
                            hasData = true;
                            int idRestaurant = resultSet.getInt("Id_Restaurant");
                            String nom = resultSet.getString("nom");
                            String adresse = resultSet.getString("adresse");
                            String phone = resultSet.getString("phone");
                            String email = resultSet.getString("email");
                            String openingHours = resultSet.getString("opening_hours");
                            String description = resultSet.getString("description");

                            // Carte Bootstrap pour chaque restaurant
                            out.println("<div class='col-md-4 mb-4'>");
                            out.println("<div class='card shadow'>");
                            out.println("<div class='card-body'>");
                            out.println("<h5 class='card-title'>" + nom + "</h5>");
                            out.println("<p class='card-text'>");
                            out.println("<strong>Adresse :</strong> " + adresse + "<br>");
                            out.println("<strong>Téléphone :</strong> " + phone + "<br>");
                            out.println("<strong>Email :</strong> " + email + "<br>");
                            out.println("<strong>Horaires :</strong> " + openingHours + "<br>");
                            out.println("<strong>Description :</strong> " + description);
                            out.println("</p>");
                            out.println("<a href='reservation?id=" + idRestaurant + "' class='btn btn-primary'>Réserver</a>");
                            out.println("</div>");
                            out.println("</div>");
                            out.println("</div>");
                        }

                        // Message si aucun restaurant n'est trouvé
                        if (!hasData) {
                            out.println("<div class='col-12'>");
                            out.println("<p class='text-center'>Aucun restaurant disponible pour le moment.</p>");
                            out.println("</div>");
                        }
                    }
                }
            } catch (Exception e) {
                out.println("<div class='alert alert-danger'>Erreur : " + e.getMessage() + "</div>");
            }

            // Fin du conteneur principal
            out.println("</div>"); // Fin de la row
            out.println("</div>"); // Fin de la container



            // Scripts Bootstrap
            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
