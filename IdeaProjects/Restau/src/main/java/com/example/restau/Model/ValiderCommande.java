package com.example.restau.Model;

public class ValiderCommande {
    private int idCommande;
    private String dateOrder;
    private String menuTitle;
    private int quantity;
    private double totalPrix;
    private String status;

    // Constructeur avec paramètres
    public ValiderCommande(int idCommande, String dateOrder, String menuTitle, int quantity, double totalPrix, String status) {
        this.idCommande = idCommande;
        this.dateOrder = dateOrder;
        this.menuTitle = menuTitle;
        this.quantity = quantity;
        this.totalPrix = totalPrix;
        this.status = status;
    }

    // Getters et setters
    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrix() {
        return totalPrix;
    }

    public void setTotalPrix(double totalPrix) {
        this.totalPrix = totalPrix;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status =status;
}
}