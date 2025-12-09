package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.michael.ebeano.models.ProductItem;

public class ExploreFragment extends Fragment {

    private MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.explore));
            toolbar.getMenu().clear();
        }

        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.child_container, new ExploreListFragment(), "explore_list")
                    .commit();
        }

        // Back handling: pop child stack first
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });

        // Listen for child back stack changes to update title and nav icon
        getChildFragmentManager().addOnBackStackChangedListener(this::syncToolbar);
        syncToolbar();
    }

    // No toolbar menu actions on Explore/Product Detail

    private void syncToolbar() {
        if (toolbar == null) return;
        Fragment current = getChildFragmentManager().findFragmentById(R.id.child_container);
        boolean canGoBack = getChildFragmentManager().getBackStackEntryCount() > 0;
        if (canGoBack) {
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> getChildFragmentManager().popBackStack());
        } else {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
        if (current instanceof ProductDetailFragment) {
            // Try to set title to product name via arguments
            Bundle args = current.getArguments();
            String title = getString(R.string.product_detail);
            if (args != null) {
                String nm = args.getString("name");
                if (nm != null && !nm.isEmpty()) title = nm;
            }
            toolbar.setTitle(title);
        } else {
            toolbar.setTitle(getString(R.string.explore));
        }
    }

    public void navigateToProductDetail(ProductItem item) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.child_container, ProductDetailFragment.newInstance(item), "product_detail")
                .addToBackStack("product_detail")
                .commit();
        syncToolbar();
    }
}
