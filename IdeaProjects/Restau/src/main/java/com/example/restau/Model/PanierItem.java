package com.example.restau.Model;

public class PanierItem {
    private int idMenu;
    private String title;
    private int quantity;
    private double totalPrice;

    public PanierItem(int idMenu, String title, int quantity, double totalPrice) {
        this.idMenu = idMenu;
        this.title = title;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
