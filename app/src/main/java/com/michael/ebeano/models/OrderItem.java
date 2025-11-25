package com.michael.ebeano.models;

public class OrderItem {
    public String productId;
    public String name;
    public double price;
    public int qty;
    public String imageUrl;
    public OrderItem() {}
    public OrderItem(String productId, String name, double price, int qty, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.imageUrl = imageUrl;
    }
}
