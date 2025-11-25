package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michael.ebeano.models.CartLine;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.Listener {
    RecyclerView recycler;
    TextView subtotal;
    TextView tax;
    TextView total;
    Button checkout;
    CartAdapter adapter;
    List<CartLine> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recycler = findViewById(R.id.recycler);
        subtotal = findViewById(R.id.subtotal);
        tax = findViewById(R.id.tax);
        total = findViewById(R.id.total);
        checkout = findViewById(R.id.btnCheckout);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(data, this);
        recycler.setAdapter(adapter);
        checkout.setOnClickListener(v -> startActivity(new Intent(this, CheckoutActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {
        data.clear();
        data.addAll(CartManager.get().all());
        adapter.notifyDataSetChanged();
        subtotal.setText(String.format("$%.2f", CartManager.get().subtotal()));
        tax.setText(String.format("$%.2f", CartManager.get().tax()));
        total.setText(String.format("$%.2f", CartManager.get().total()));
    }

    @Override
    public void onChanged() {
        refresh();
    }
}