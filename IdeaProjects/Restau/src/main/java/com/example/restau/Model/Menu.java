package com.example.restau.Model;

public class Menu {
    private int idMenu;
    private String title;
    private String description;
    private String categorie;
    private int quantity;
    private double prix;

    // Constructeur
    public Menu(int idMenu, String title, String description, String categorie, int quantity, double prix) {
        this.idMenu = idMenu;
        this.title = title;
        this.description = description;
        this.categorie = categorie;
        this.quantity = quantity;
        this.prix = prix;
    }

    // Getters et setters
    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}
