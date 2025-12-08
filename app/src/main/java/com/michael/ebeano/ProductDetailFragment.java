package com.michael.ebeano;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.michael.ebeano.models.ProductItem;

import java.util.Locale;

public class ProductDetailFragment extends Fragment {

    public static ProductDetailFragment newInstance(ProductItem item) {
        Bundle b = new Bundle();
        b.putString("id", item.id);
        b.putString("name", item.name);
        b.putString("shortDescription", item.shortDescription);
        b.putString("longDescription", item.longDescription);
        b.putDouble("price", item.price);
        b.putString("imageUrl", item.imageUrl);
        ProductDetailFragment f = new ProductDetailFragment();
        f.setArguments(b);
        return f;
    }

    ImageView image;
    TextView name;
    TextView price;
    TextView desc;
    EditText qty;
    Button add;
    ProductItem item;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        desc = view.findViewById(R.id.desc);
        qty = view.findViewById(R.id.qty);
        add = view.findViewById(R.id.btnAdd);

        Bundle args = getArguments();
        item = new ProductItem();
        if (args != null) {
            item.id = args.getString("id");
            item.name = args.getString("name");
            item.shortDescription = args.getString("shortDescription");
            item.longDescription = args.getString("longDescription");
            item.price = args.getDouble("price", 0);
            item.imageUrl = args.getString("imageUrl");
        }

        name.setText(item.name);
        price.setText(String.format(Locale.getDefault(), "$%.2f", item.price));
        desc.setText(item.longDescription);
        qty.setText("1");
        Glide.with(this).load(item.imageUrl).into(image);
        add.setOnClickListener(v -> {
            CartManager.get().add(item, parseQty());
            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show();
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
