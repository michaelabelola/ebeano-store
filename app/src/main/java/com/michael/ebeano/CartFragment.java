package com.michael.ebeano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CartFragment extends Fragment {

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
                    .replace(R.id.child_container, new CartListFragment(), "cart_list")
                    .commit();
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getChildFragmentManager().getBackStackEntryCount() > 0) {
                    getChildFragmentManager().popBackStack();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    public void openCheckout() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.child_container, new CheckoutFragment(), "checkout")
                .addToBackStack("checkout")
                .commit();
    }

    public void openThankYou() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.child_container, new ThankYouFragment(), "thank_you")
                .addToBackStack("thank_you")
                .commit();
    }

    public void resetToCart() {
        // Clear child back stack and show cart list
        while (getChildFragmentManager().getBackStackEntryCount() > 0) {
            getChildFragmentManager().popBackStackImmediate();
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.child_container, new CartListFragment(), "cart_list")
                .commit();
    }
}
