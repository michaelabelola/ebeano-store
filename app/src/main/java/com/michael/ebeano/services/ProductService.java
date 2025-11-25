package com.michael.ebeano.services;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.michael.ebeano.models.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    public interface Result<T> { void onResult(T data); }
    final CollectionReference col = FirebaseFirestore.getInstance().collection("products");

    public void list(Result<List<ProductItem>> cb) {
        col.get().addOnSuccessListener(snap -> {
            List<ProductItem> list = new ArrayList<>();
            for (QueryDocumentSnapshot d : snap) {
                ProductItem p = d.toObject(ProductItem.class);
                p.id = d.getId();
                list.add(p);
            }
            cb.onResult(list);
        });
    }

    public void add(ProductItem p, Result<ProductItem> cb) {
        col.add(p).addOnSuccessListener(ref -> ref.get().addOnSuccessListener(d -> {
            ProductItem i = d.toObject(ProductItem.class);
            i.id = d.getId();
            cb.onResult(i);
        }));
    }

    public void seedDefault(Result<List<ProductItem>> cb) {
        list(items -> {
            if (!items.isEmpty()) { cb.onResult(items); return; }
            List<ProductItem> seeds = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                ProductItem p = new ProductItem();
                p.name = "Item " + i;
                p.shortDescription = "Short desc " + i;
                p.longDescription = "Long description of item " + i;
                p.price = 9.99 + i;
                p.imageUrl = "https://picsum.photos/seed/" + i + "/400/400";
                seeds.add(p);
            }
            List<ProductItem> created = new ArrayList<>();
            for (ProductItem s : seeds) {
                add(s, pi -> {
                    created.add(pi);
                    if (created.size() == seeds.size()) cb.onResult(created);
                });
            }
        });
    }
}
