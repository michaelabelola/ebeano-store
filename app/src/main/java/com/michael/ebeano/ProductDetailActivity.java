package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.michael.ebeano.models.ProductItem;

public class ProductDetailActivity extends AppCompatActivity {
    ImageView image;
    TextView name;
    TextView price;
    TextView desc;
    EditText qty;
    Button add;
    Button goCart;
    ProductItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        desc = findViewById(R.id.desc);
        qty = findViewById(R.id.qty);
        add = findViewById(R.id.btnAdd);
        goCart = findViewById(R.id.btnGoCart);
        item = new ProductItem();
        item.id = getIntent().getStringExtra("id");
        item.name = getIntent().getStringExtra("name");
        item.shortDescription = getIntent().getStringExtra("shortDescription");
        item.longDescription = getIntent().getStringExtra("longDescription");
        item.price = getIntent().getDoubleExtra("price", 0);
        item.imageUrl = getIntent().getStringExtra("imageUrl");
        name.setText(item.name);
        price.setText(String.format("$%.2f", item.price));
        desc.setText(item.longDescription);
        qty.setText("1");
        Glide.with(this).load(item.imageUrl).into(image);
        add.setOnClickListener(v -> CartManager.get().add(item, parseQty()));
        goCart.setOnClickListener(v -> {
            CartManager.get().add(item, parseQty());
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    int parseQty() {
        String t = qty.getText().toString().trim();
        if (TextUtils.isEmpty(t)) return 1;
        try {
            int q = Integer.parseInt(t);
            return Math.max(1, q);
        } catch (Exception e) {
            return 1;
        }
    }
}