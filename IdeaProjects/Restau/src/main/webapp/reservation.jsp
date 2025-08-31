<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Réservation</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100" style="
    background-image: url('images/home.jpg');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;">
<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand" href="#">MyApp</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <a class="nav-link" href="#">Accueil</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#">À propos</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="#">Contact</a>
        </li>
      </ul>
    </div>
  </div>
</nav>

<!-- Formulaire de réservation -->
<div class="container mt-5">
  <div class="card bg-light shadow">
    <div class="card-header text-white text-center" style="background-color: rgb(82,80,80);">
      <h2>Réservation pour le restaurant</h2>
    </div>
    <div class="card-body">
      <h5 class="card-title mb-4 text-center">
        Restaurant : <%= request.getAttribute("restaurantName") %>
      </h5>
      <form method="post" action="reservation">
        <input type="hidden" name="restaurant_id" value="<%= request.getAttribute("restaurantId") %>">
        <div class="mb-3">
          <label for="client_name" class="form-label">Nom du client :</label>
          <input type="text" id="client_name" name="client_name" class="form-control" required>
        </div>
        <div class="mb-3">
          <label for="date" class="form-label">Date :</label>
          <input type="date" id="date" name="date" class="form-control" required>
        </div>
        <div class="mb-3">
          <label for="time" class="form-label">Heure :</label>
          <input type="time" id="time" name="time" class="form-control" required>
        </div>
        <div class="mb-3">
          <label for="numero_person" class="form-label">Nombre de personnes :</label>
          <input type="number" id="numero_person" name="numero_person" class="form-control" required>
        </div>
        <div class="mb-3">
          <label for="status" class="form-label">Status :</label>
          <select id="status" name="status" class="form-select">
            <option value="En attente">En attente</option>
            <option value="Confirmée">Confirmée</option>
          </select>
        </div>
        <div class="text-center">
          <button type="submit" class="btn" style="background-color: rgb(82, 80, 80); border-color: rgb(82, 80, 80); color: white;">Confirmer la réservation</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Footer -->
<footer class="bg-dark text-white text-center py-3 mt-5">
  <div class="container">
    <p class="mb-0">&copy; 2024 RestaurantApp. Tous droits réservés.</p>
  </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
