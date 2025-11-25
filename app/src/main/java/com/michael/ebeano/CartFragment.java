package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michael.ebeano.models.CartLine;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.Listener {

    RecyclerView recycler;
    TextView subtotal;
    TextView tax;
    TextView total;
    Button checkout;
    CartAdapter adapter;
    final List<CartLine> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reuse the existing activity layout for minimal changes
        return inflater.inflate(R.layout.activity_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = view.findViewById(R.id.recycler);
        subtotal = view.findViewById(R.id.subtotal);
        tax = view.findViewById(R.id.tax);
        total = view.findViewById(R.id.total);
        checkout = view.findViewById(R.id.btnCheckout);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CartAdapter(data, this);
        recycler.setAdapter(adapter);
        checkout.setOnClickListener(v -> startActivity(new Intent(requireContext(), CheckoutActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {
        data.clear();
        data.addAll(CartManager.get().all());
        if (adapter != null) adapter.notifyDataSetChanged();
        if (subtotal != null) subtotal.setText(String.format("$%.2f", CartManager.get().subtotal()));
        if (tax != null) tax.setText(String.format("$%.2f", CartManager.get().tax()));
        if (total != null) total.setText(String.format("$%.2f", CartManager.get().total()));
    }

    @Override
    public void onChanged() {
        refresh();
    }
}
