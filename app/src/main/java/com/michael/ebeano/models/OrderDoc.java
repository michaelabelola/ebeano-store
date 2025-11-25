package com.michael.ebeano.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class OrderDoc {
    public String id;
    public String userId;
    public String firstName;
    public String lastName;
    public String address;
    public String email;
    public String phone;
    public String paymentMethod;
    public double subtotal;
    public double tax;
    public double total;
    public List<OrderItem> items;
    public Timestamp createdAt;
    public OrderDoc() {}
}
