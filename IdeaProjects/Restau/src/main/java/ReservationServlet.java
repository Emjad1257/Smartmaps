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
import java.sql.Statement;

@WebServlet("/reservation")
public class ReservationServlet extends HttpServlet {

    @Resource(name = "jdbc/restauDS")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String restaurantId = request.getParameter("id");
        String restaurantName = "";

        // Récupération du nom du restaurant
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT nom FROM restaurant WHERE Id_Restaurant = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, Integer.parseInt(restaurantId));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        restaurantName = resultSet.getString("nom");
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la récupération du restaurant", e);
        }

        // Envoyer les données à la JSP
        request.setAttribute("restaurantId", restaurantId);
        request.setAttribute("restaurantName", restaurantName);
        request.getRequestDispatcher("/reservation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupération des données du formulaire
        int restaurantId = Integer.parseInt(request.getParameter("restaurant_id"));
        String clientName = request.getParameter("client_name");
        String date = request.getParameter("date");
        String time = request.getParameter("time");
        int numeroPerson = Integer.parseInt(request.getParameter("numero_person"));
        String status = request.getParameter("status");

        int generatedReservationId = 0;

        // Insertion dans la table reservation et récupération de l'ID généré
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO reservation (restaurant_id, Date, Time, NumeroPerson, Status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, restaurantId);
                statement.setString(2, date);
                statement.setString(3, time);
                statement.setInt(4, numeroPerson);
                statement.setString(5, status);
                statement.executeUpdate();

                // Récupérer l'ID de la réservation
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedReservationId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'insertion de la réservation", e);
        }

        // Redirection vers la page des tables avec l'ID de la réservation
        response.sendRedirect("table-reservation?reservation_id=" + generatedReservationId);
    }
}
