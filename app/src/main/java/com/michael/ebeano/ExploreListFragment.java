package com.michael.ebeano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michael.ebeano.models.ProductItem;
import com.michael.ebeano.services.ProductService;

import java.util.ArrayList;

public class ExploreListFragment extends Fragment implements ProductAdapter.Listener {

    RecyclerView list;
    ProgressBar progress;
    ProductAdapter adapter;
    ProductService service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reuse the existing activity layout for product listing
        return inflater.inflate(R.layout.activity_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(R.id.recycler);
        progress = view.findViewById(R.id.progress);
        int orientation = getResources().getConfiguration().orientation;
        int span = orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        list.setLayoutManager(new GridLayoutManager(requireContext(), span));
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
        Fragment parent = getParentFragment();
        if (parent instanceof ExploreFragment) {
            ((ExploreFragment) parent).navigateToProductDetail(item);
        }
    }
}
