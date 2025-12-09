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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.michael.ebeano.models.ProductItem;
import com.michael.ebeano.services.ProductService;

import java.util.ArrayList;

public class ExploreListFragment extends Fragment implements ProductAdapter.Listener {

    RecyclerView list;
    ProgressBar progress;
    SwipeRefreshLayout swipeRefresh;
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
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        int orientation = getResources().getConfiguration().orientation;
        int span = orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        list.setLayoutManager(new GridLayoutManager(requireContext(), span));
        adapter = new ProductAdapter(new ArrayList<>(), this);
        list.setAdapter(adapter);
        service = new ProductService();
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeColors(0xFF000000); // black
            swipeRefresh.setOnRefreshListener(() -> load(true));
        }
        load(false);
    }

    void load() { load(false); }

    void load(boolean fromSwipe) {
        if (!fromSwipe && progress != null) progress.setVisibility(View.VISIBLE);
        if (fromSwipe && swipeRefresh != null && !swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(true);
        service.list(items -> {
            if (items.isEmpty()) {
                service.seedDefault(created -> {
                    adapter.setData(created);
                    if (progress != null) progress.setVisibility(View.GONE);
                    if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                });
            } else {
                adapter.setData(items);
                if (progress != null) progress.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
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
