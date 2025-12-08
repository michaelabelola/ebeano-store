package com.michael.ebeano;

import android.content.Context;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.michael.ebeano.models.CartLine;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
    public interface Listener { void onChanged(); }
    List<CartLine> data;
    Listener listener;
    final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());

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
        h.price.setText(currency.format(l.item.price));
        h.qty.setText(String.valueOf(l.qty));
        h.lineTotal.setText(currency.format(l.item.price * l.qty));

        h.plus.setOnClickListener(v -> {
            CartManager.get().update(l.item.id, l.qty + 1);
            listener.onChanged();
        });

        h.minus.setOnClickListener(v -> {
            if (l.qty <= 1) {
                showRemoveDialog(v.getContext(), () -> { CartManager.get().remove(l.item.id); listener.onChanged(); });
            } else {
                CartManager.get().update(l.item.id, l.qty - 1);
                listener.onChanged();
            }
        });

        h.remove.setOnClickListener(v -> showRemoveDialog(v.getContext(), () -> { CartManager.get().remove(l.item.id); listener.onChanged(); }));

        h.qty.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String t = h.qty.getText().toString().trim();
                if (TextUtils.isEmpty(t)) return;
                try {
                    int q = Integer.parseInt(t);
                    if (q <= 0) {
                        showRemoveDialog(v.getContext(), () -> { CartManager.get().remove(l.item.id); listener.onChanged(); });
                    } else {
                        CartManager.get().update(l.item.id, q);
                        listener.onChanged();
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private void showRemoveDialog(Context c, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(c)
                .setTitle(R.string.remove_item)
                .setMessage(R.string.remove_item_question)
                .setPositiveButton(R.string.remove, (d, w) -> onConfirm.run())
                .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                .show();
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        TextView lineTotal;
        EditText qty;
        Button plus;
        Button minus;
        Button remove;
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            lineTotal = itemView.findViewById(R.id.line_total);
            qty = itemView.findViewById(R.id.qty);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}