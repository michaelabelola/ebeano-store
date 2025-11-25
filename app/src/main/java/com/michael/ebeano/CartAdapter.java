package com.michael.ebeano;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.michael.ebeano.models.CartLine;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
    public interface Listener { void onChanged(); }
    List<CartLine> data;
    Listener listener;
    public CartAdapter(List<CartLine> data, Listener listener) { this.data = data; this.listener = listener; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartLine l = data.get(position);
        Glide.with(h.image.getContext()).load(l.item.imageUrl).into(h.image);
        h.name.setText(l.item.name);
        h.price.setText(String.format("$%.2f", l.item.price));
        h.qty.setText(String.valueOf(l.qty));
        h.plus.setOnClickListener(v -> { CartManager.get().update(l.item.id, l.qty + 1); listener.onChanged(); });
        h.minus.setOnClickListener(v -> { CartManager.get().update(l.item.id, l.qty - 1); listener.onChanged(); });
        h.remove.setOnClickListener(v -> { CartManager.get().remove(l.item.id); listener.onChanged(); });
        h.qty.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String t = h.qty.getText().toString().trim();
                if (TextUtils.isEmpty(t)) return;
                try { int q = Integer.parseInt(t); CartManager.get().update(l.item.id, q); listener.onChanged(); } catch (Exception ignored) {}
            }
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        EditText qty;
        Button plus;
        Button minus;
        Button remove;
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            qty = itemView.findViewById(R.id.qty);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}