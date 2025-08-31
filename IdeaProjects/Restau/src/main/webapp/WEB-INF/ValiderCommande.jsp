<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Valider la Commande</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> <!-- Ajout SweetAlert -->
</head>
<body>
<div class="container mt-5">
    <h1 class="text-center mb-4">Valider la Commande</h1>

    <!-- Formulaire pour valider la commande -->
    <form id="commandeForm" action="ValiderCommande" method="POST">
        <div class="mb-3">
            <label for="idMenu" class="form-label">ID du Menu</label>
            <input type="text" class="form-control" id="idMenu" name="idMenu" value="${param.idMenu}" readonly>
        </div>

        <div class="mb-3">
            <label for="dateOrder" class="form-label">Date de Commande</label>
            <input type="date" class="form-control" id="dateOrder" name="dateOrder" value="<%= java.time.LocalDate.now() %>" required>
        </div>

        <div class="mb-3">
            <label for="status" class="form-label">Statut</label>
            <select class="form-select" id="status" name="status">
                <option value="En Cours" selected>En Cours</option>
                <option value="Confirmée">Confirmée</option>
                <option value="Annulée">Annulée</option>
            </select>
        </div>

        <!-- Liste déroulante des serveurs -->
        <div class="mb-3">
            <label for="idServeur" class="form-label">Choisir un Serveur</label>
            <select class="form-select" id="idServeur" name="idServeur" required>
                <option value="">-- Sélectionnez un serveur --</option>
                <c:forEach var="serveur" items="${serveurs}">
                    <option value="${serveur.id}">${serveur.nom}</option>
                </c:forEach>
            </select>
        </div>

        <div class="text-center">
            <button type="button" id="submitCommande" class="btn btn-success">Valider la Commande</button>
            <a href="menu.jsp" class="btn btn-secondary">Retour au Menu</a>
        </div>
    </form>
</div>

<script>
    document.getElementById("submitCommande").addEventListener("click", function () {
        // Utilisation de SweetAlert pour afficher une confirmation
        Swal.fire({
            title: 'Êtes-vous sûr ?',
            text: "Vous êtes sur le point de valider cette commande.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Oui, valider',
            cancelButtonText: 'Annuler'
        }).then((result) => {
            if (result.isConfirmed) {
                // Soumettre le formulaire si l'utilisateur confirme
                document.getElementById("commandeForm").submit();
            }
        });
    });
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>