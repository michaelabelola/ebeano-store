package com.michael.ebeano;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.snackbar.Snackbar;
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
    TextView shortDesc;
    TextView desc;
    TextView subtotal;
    EditText qty;
    View btnMinus;
    View btnPlus;
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
        shortDesc = view.findViewById(R.id.shortDesc);
        desc = view.findViewById(R.id.desc);
        subtotal = view.findViewById(R.id.subtotal);
        qty = view.findViewById(R.id.qty);
        add = view.findViewById(R.id.btnAdd);
        btnMinus = view.findViewById(R.id.btnMinus);
        btnPlus = view.findViewById(R.id.btnPlus);

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

        // Descriptions
        if (shortDesc != null) {
            if (!TextUtils.isEmpty(item.shortDescription)) {
                shortDesc.setText(item.shortDescription);
                shortDesc.setVisibility(View.VISIBLE);
            } else {
                shortDesc.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(item.longDescription)) {
            desc.setText(item.longDescription);
            desc.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(item.shortDescription)) {
            desc.setText(item.shortDescription);
            desc.setVisibility(View.VISIBLE);
        } else {
            desc.setText(R.string.no_description);
            desc.setVisibility(View.VISIBLE);
        }

        // Quantity and subtotal
        qty.setText("1");
        qty.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) { updateSubtotal(); }
        });
        if (btnMinus != null) btnMinus.setOnClickListener(v -> {
            int q = Math.max(1, parseQty() - 1);
            qty.setText(String.valueOf(q));
        });
        if (btnPlus != null) btnPlus.setOnClickListener(v -> {
            int q = Math.min(99, parseQty() + 1);
            qty.setText(String.valueOf(q));
        });
        updateSubtotal();

        // Image loading with placeholder/error and accessibility
        image.setContentDescription(item.name != null ? item.name : getString(R.string.product_image));
        Glide.with(this)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(image);

        add.setOnClickListener(v -> {
            CartManager.get().add(item, parseQty());
            Snackbar.make(view, R.string.added_to_cart, Snackbar.LENGTH_SHORT).show();
        });
    }

    int parseQty() {
        String t = qty.getText().toString().trim();
        if (TextUtils.isEmpty(t)) return 1;
        try {
            int q = Integer.parseInt(t);
            if (q < 1) return 1;
            if (q > 99) return 99;
            return q;
        } catch (Exception e) {
            return 1;
        }
    }

    void updateSubtotal() {
        if (subtotal == null) return;
        int q = parseQty();
        double sub = item != null ? item.price * q : 0;
        subtotal.setText(getString(R.string.subtotal_fmt, String.format(Locale.getDefault(), "$%.2f", sub)));
    }
}
