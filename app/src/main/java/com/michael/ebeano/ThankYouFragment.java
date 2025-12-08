package com.michael.ebeano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ThankYouFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_thank_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button back = view.findViewById(R.id.btnBackToProducts);
        back.setOnClickListener(v -> {
            // Reset Cart host back to cart list
            Fragment parent = getParentFragment();
            if (parent instanceof CartFragment) {
                ((CartFragment) parent).resetToCart();
            }
            // Switch to Explore tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openExploreTab();
            }
        });
    }
}
