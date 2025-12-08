package com.michael.ebeano;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.michael.ebeano.models.ProductItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
    public interface Listener {
        void onAddToCart(ProductItem item);
        void onOpen(ProductItem item);
    }

    List<ProductItem> data;
    Listener listener;
    final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());

    public ProductAdapter(List<ProductItem> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(List<ProductItem> d) {
        this.data = d;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_explore_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductItem p = data.get(position);
        h.name.setText(p.name);
        h.price.setText(currency.format(p.price));
        h.desc.setText(p.shortDescription);
        Glide.with(h.image.getContext())
                .load(p.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(h.image);
        h.itemView.setOnClickListener(v -> listener.onOpen(p));
        h.add.setOnClickListener(v -> listener.onAddToCart(p));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;
        TextView desc;
        Button add;
        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            desc = itemView.findViewById(R.id.desc);
            add = itemView.findViewById(R.id.btnAdd);
        }
    }
}