<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Serveurs</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5">
    <h1 class="text-center mb-4">Liste des Serveurs</h1>

    <!-- Tableau pour afficher les serveurs -->
    <table class="table table-striped table-bordered">
        <thead class="table-dark">
        <tr>
            <th>ID Serveur</th>
            <th>Nom</th>
            <th>Numéro de Téléphone</th>
            <th>Shift</th>
            <th>ID Restaurant</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="serveur" items="${serveurs}">
            <tr>
                <td>${serveur.idServeurs}</td>
                <td>${serveur.nom}</td>
                <td>${serveur.numeroTele}</td>
                <td>${serveur.shift}</td>
                <td>${serveur.idRestaurant}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="text-center mt-3">
        <a href="index.jsp" class="btn btn-primary">Retour à l'accueil</a>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
