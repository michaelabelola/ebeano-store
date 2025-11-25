package com.michael.ebeano.models;

public class CartLine {
    public ProductItem item;
    public int qty;
    public CartLine() {}
    public CartLine(ProductItem item, int qty) { this.item = item; this.qty = qty; }
}