package com.michael.ebeano;

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

public class CartListFragment extends Fragment implements CartAdapter.Listener {

    RecyclerView recycler;
    TextView subtotal;
    TextView tax;
    TextView total;
    Button checkout;
    View emptyState;
    Button continueShopping;
    CartAdapter adapter;
    final List<CartLine> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = view.findViewById(R.id.recycler);
        subtotal = view.findViewById(R.id.subtotal);
        tax = view.findViewById(R.id.tax);
        total = view.findViewById(R.id.total);
        checkout = view.findViewById(R.id.btnCheckout);
        emptyState = view.findViewById(R.id.emptyState);
        continueShopping = view.findViewById(R.id.btnContinueShopping);

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CartAdapter(data, this);
        recycler.setAdapter(adapter);
        checkout.setOnClickListener(v -> {
            Fragment parent = getParentFragment();
            if (parent instanceof CartFragment) {
                ((CartFragment) parent).openCheckout();
            }
        });

        continueShopping.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openExploreTab();
            }
        });
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
        java.text.NumberFormat currency = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault());
        if (subtotal != null) subtotal.setText(currency.format(CartManager.get().subtotal()));
        if (tax != null) tax.setText(currency.format(CartManager.get().tax()));
        if (total != null) total.setText(currency.format(CartManager.get().total()));

        boolean isEmpty = data.isEmpty();
        if (recycler != null) recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (emptyState != null) emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (checkout != null) checkout.setEnabled(!isEmpty);
    }

    @Override
    public void onChanged() {
        refresh();
    }
}
