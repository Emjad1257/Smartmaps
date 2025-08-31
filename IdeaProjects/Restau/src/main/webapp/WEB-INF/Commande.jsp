<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Commandes</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5">
    <h1 class="text-center mb-4">Liste des Commandes</h1>

    <!-- Section pour afficher les commandes -->
    <table class="table table-striped">
        <thead>
        <tr>
            <th>#</th>
            <th>Date</th>
            <th>Menu</th>
            <th>Quantité</th>
            <th>Prix Total</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${not empty commandes}">
            <c:forEach var="commande" items="${commandes}">
                <tr>
                    <td>${commande.idCommande}</td>
                    <td>${commande.dateOrder}</td>
                    <td>${commande.menuTitle}</td>
                    <td>${commande.quantity}</td>
                    <td>${commande.totalPrix} MAD</td>
                    <td>${commande.status}</td>
                </tr>
            </c:forEach>
        </c:if>
        <c:if test="${empty commandes}">
            <tr>
                <td colspan="6" class="text-center">Aucune commande disponible pour le moment.</td>
            </tr>
        </c:if>
        </tbody>
    </table>

    <!-- Section pour afficher les menus disponibles -->
    <h2 class="text-center mt-5 mb-4">Menus disponibles</h2>
    <div class="row">
        <c:forEach var="menu" items="${menus}">
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${menu.title}</h5>
                        <p class="card-text">${menu.description}</p>
                        <p><strong>Prix : </strong>${menu.price} MAD</p>
                        <form action="Commande" method="POST">
                            <input type="hidden" name="idMenu" value="${menu.id}">
                            <input type="hidden" name="prix" value="${menu.price}">
                            <label for="quantity-${menu.id}" class="form-label">Quantité</label>
                            <input type="number" class="form-control mb-2" id="quantity-${menu.id}" name="quantity" min="1" value="1">
                            <button type="submit" class="btn btn-primary w-100">Ajouter</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
