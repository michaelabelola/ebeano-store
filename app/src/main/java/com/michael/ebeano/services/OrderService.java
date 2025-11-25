package com.michael.ebeano.services;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.michael.ebeano.models.OrderDoc;

public class OrderService {
    public interface Result<T> { void onResult(T data); }
    final CollectionReference col = FirebaseFirestore.getInstance().collection("orders");

    public void create(OrderDoc order, Result<String> cb) {
        col.add(order).addOnSuccessListener(r -> cb.onResult(r.getId()));
    }
}
