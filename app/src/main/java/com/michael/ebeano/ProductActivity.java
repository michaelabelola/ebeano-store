package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.michael.ebeano.models.ProductItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.Listener {
    RecyclerView list;
    ProgressBar progress;
    ProductAdapter adapter;
    FirebaseFirestore db;
    CollectionReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        list = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(new ArrayList<>(), this);
        list.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        productsRef = db.collection("products");
        load();
    }

    void load() {
        progress.setVisibility(View.VISIBLE);
        productsRef.get().addOnSuccessListener(snap -> {
            List<ProductItem> items = new ArrayList<>();
            for (QueryDocumentSnapshot d : snap) {
                ProductItem p = d.toObject(ProductItem.class);
                p.id = d.getId();
                items.add(p);
            }
            if (items.isEmpty()) seed(); else {
                adapter.setData(items);
                progress.setVisibility(View.GONE);
            }
        });
    }

    void seed() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", "Item " + i);
            m.put("shortDescription", "Short desc " + i);
            m.put("longDescription", "Long description of item " + i);
            m.put("price", 9.99 + i);
            m.put("imageUrl", "https://picsum.photos/seed/" + i + "/400/400");
            list.add(m);
        }
        List<ProductItem> items = new ArrayList<>();
        for (Map<String, Object> m : list) {
            productsRef.add(m).addOnSuccessListener(ref -> ref.get().addOnSuccessListener(d -> {
                ProductItem p = d.toObject(ProductItem.class);
                p.id = d.getId();
                items.add(p);
                if (items.size() == 8) {
                    adapter.setData(items);
                    progress.setVisibility(View.GONE);
                }
            }));
        }
    }

    @Override
    public void onAddToCart(ProductItem item) {
        CartManager.get().add(item, 1);
    }

    @Override
    public void onOpen(ProductItem item) {
        Intent i = new Intent(this, ProductDetailActivity.class);
        i.putExtra("id", item.id);
        i.putExtra("name", item.name);
        i.putExtra("shortDescription", item.shortDescription);
        i.putExtra("longDescription", item.longDescription);
        i.putExtra("price", item.price);
        i.putExtra("imageUrl", item.imageUrl);
        startActivity(i);
    }
}