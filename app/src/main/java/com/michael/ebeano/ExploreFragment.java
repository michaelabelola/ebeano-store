package com.michael.ebeano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.michael.ebeano.models.ProductItem;

public class ExploreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_host, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.child_container, new ExploreListFragment(), "explore_list")
                    .commit();
        }
    }

    public void navigateToProductDetail(ProductItem item) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.child_container, ProductDetailFragment.newInstance(item), "product_detail")
                .addToBackStack("product_detail")
                .commit();
    }
}
