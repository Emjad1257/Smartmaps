package com.example.restau.Model;

public class Serveur {
    private int id; // Correction : nom de l'attribut doit être "id"
    private String nom;

    public Serveur(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Serveur(int idServeurs, String nom, String numerotele, String shift, int idRestaurant) {
        this.id = id;
    }

    // Getter et Setter pour id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter et Setter pour nom
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
