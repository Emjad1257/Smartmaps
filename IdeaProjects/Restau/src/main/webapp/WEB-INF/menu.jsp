<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menus du Restaurant</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css">
    <style>
        .sidebar {
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            background-color: #f8f9fa;
            padding-top: 20px;
            width: 250px;
        }

        .content {
            margin-left: 270px;
            padding: 20px;
        }

        .sidebar a {
            color: #000;
            text-decoration: none;
            display: block;
            padding: 10px 15px;
            transition: background-color 0.3s;
        }

        .sidebar a:hover, .sidebar a.active {
            background-color: #007bff;
            color: #fff !important;
        }

        .section {
            display: none;
        }

        .section.active {
            display: block;
        }
    </style>
</head>
<body>
<div class="sidebar">
    <h4 class="text-center">Catégories</h4>
    <a href="#plats-principaux" class="active" onclick="showSection('plats-principaux')">Plats Principaux</a>
    <a href="#boissons" onclick="showSection('boissons')">Boissons</a>
    <a href="#desserts" onclick="showSection('desserts')">Desserts</a>
    <a href="#panier" onclick="showSection('panier')">Panier</a>
</div>

<div class="content">
    <h1 class="text-center mb-4">Menus du Restaurant</h1>

    <!-- Plats Principaux -->
    <div id="plats-principaux" class="section active">
        <h2>Plats Principaux</h2>
        <c:forEach var="menu" items="${menus}">
            <c:if test="${menu.categorie == 'Plat Principal'}">
                <div class="col-md-4">
                    <div class="card mb-4">
                        <div class="card-body">
                            <h5 class="card-title">${menu.title != null ? menu.title : 'Titre non disponible'}</h5>
                            <p class="card-text">${menu.description != null ? menu.description : 'Description non disponible'}</p>
                            <p class="text-muted">Prix : ${menu.prix != null ? menu.prix : 'Prix non disponible'} MAD</p>
                            <form action="Commande" method="POST">
                                <input type="hidden" name="idMenu" value="${menu.idMenu != null ? menu.idMenu : 0}">
                                <input type="hidden" name="title" value="${menu.title != null ? menu.title : ''}">
                                <input type="hidden" name="prix" value="${menu.prix != null ? menu.prix : '0'}">
                                <div class="mb-3">
                                    <label for="quantity-${menu.idMenu}" class="form-label">Quantité</label>
                                    <input type="number" class="form-control" id="quantity-${menu.idMenu}" name="quantity" min="1" value="1">
                                </div>
                                <button type="submit" class="btn btn-primary">Ajouter au Panier</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>

    <!-- Boissons -->
    <div id="boissons" class="section">
        <h2>Boissons</h2>
        <c:forEach var="menu" items="${menus}">
            <c:if test="${menu.categorie == 'Boisson'}">
                <div class="col-md-4">
                    <div class="card mb-4">
                        <div class="card-body">
                            <h5 class="card-title">${menu.title != null ? menu.title : 'Titre non disponible'}</h5>
                            <p class="card-text">${menu.description != null ? menu.description : 'Description non disponible'}</p>
                            <p class="text-muted">Prix : ${menu.prix != null ? menu.prix : 'Prix non disponible'} MAD</p>
                            <form action="Commande" method="POST">
                                <input type="hidden" name="idMenu" value="${menu.idMenu != null ? menu.idMenu : 0}">
                                <input type="hidden" name="title" value="${menu.title != null ? menu.title : ''}">
                                <input type="hidden" name="prix" value="${menu.prix != null ? menu.prix : '0'}">
                                <div class="mb-3">
                                    <label for="quantity-${menu.idMenu}" class="form-label">Quantité</label>
                                    <input type="number" class="form-control" id="quantity-${menu.idMenu}" name="quantity" min="1" value="1">
                                </div>
                                <button type="submit" class="btn btn-success">Ajouter au Panier</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>

    <!-- Desserts -->
    <div id="desserts" class="section">
        <h2>Desserts</h2>
        <c:forEach var="menu" items="${menus}">
            <c:if test="${menu.categorie == 'Dessert'}">
                <div class="col-md-4">
                    <div class="card mb-4">
                        <div class="card-body">
                            <h5 class="card-title">${menu.title != null ? menu.title : 'Titre non disponible'}</h5>
                            <p class="card-text">${menu.description != null ? menu.description : 'Description non disponible'}</p>
                            <p class="text-muted">Prix : ${menu.prix != null ? menu.prix : 'Prix non disponible'} MAD</p>
                            <form action="Commande" method="POST">
                                <input type="hidden" name="idMenu" value="${menu.idMenu != null ? menu.idMenu : 0}">
                                <input type="hidden" name="title" value="${menu.title != null ? menu.title : ''}">
                                <input type="hidden" name="prix" value="${menu.prix != null ? menu.prix : '0'}">
                                <div class="mb-3">
                                    <label for="quantity-${menu.idMenu}" class="form-label">Quantité</label>
                                    <input type="number" class="form-control" id="quantity-${menu.idMenu}" name="quantity" min="1" value="1">
                                </div>
                                <button type="submit" class="btn btn-warning">Ajouter au Panier</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>

    <!-- Panier -->
    <div id="panier" class="section">
        <h2>Votre Panier</h2>
        <c:choose>
            <c:when test="${not empty sessionScope.panier}">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Nom du Plat</th>
                        <th>Quantité</th>
                        <th>Prix Total</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${sessionScope.panier}" varStatus="status">
                        <tr>
                            <td>${status.index + 1}</td>
                            <td>${item.title}</td>
                            <td>${item.quantity}</td>
                            <td>${item.totalPrice} MAD</td>
                            <td>
                                <form action="RemoveFromCart" method="POST">
                                    <input type="hidden" name="idMenu" value="${item.idMenu}">
                                    <button type="submit" class="btn btn-danger btn-sm">Supprimer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <a href="ValiderCommande" class="btn btn-success">Valider</a>
            </c:when>
            <c:otherwise>
                <p class="text-center">Votre panier est vide.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    function showSection(sectionId) {
        document.querySelectorAll('.section').forEach(section => section.classList.remove('active'));
        document.getElementById(sectionId).classList.add('active');

        document.querySelectorAll('.sidebar a').forEach(link => link.classList.remove('active'));
        document.querySelector(`[href="#${sectionId}"]`).classList.add('active');
    }
</script>
</body>
</html>
