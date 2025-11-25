package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michael.ebeano.models.ProductItem;
import com.michael.ebeano.services.ProductService;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.Listener {
    RecyclerView list;
    ProgressBar progress;
    ProductAdapter adapter;
    ProductService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        list = findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(new ArrayList<>(), this);
        list.setAdapter(adapter);
        service = new ProductService();
        load();
    }

    void load() {
        progress.setVisibility(View.VISIBLE);
        service.list(items -> {
            if (items.isEmpty()) {
                service.seedDefault(created -> {
                    adapter.setData(created);
                    progress.setVisibility(View.GONE);
                });
            } else {
                adapter.setData(items);
                progress.setVisibility(View.GONE);
            }
        });
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