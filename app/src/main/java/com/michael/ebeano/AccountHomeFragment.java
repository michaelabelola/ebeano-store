package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.michael.ebeano.models.UserDoc;

public class AccountHomeFragment extends Fragment {

    TextView userName;
    Button btnProfile;
    Button btnAddProduct;
    MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbarAccount);
        if (toolbar != null) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_account);
            toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        }
        userName = view.findViewById(R.id.userName);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);

        btnProfile.setOnClickListener(v -> {
            Fragment parent = getParentFragment();
            if (parent instanceof AccountFragment) {
                ((AccountFragment) parent).openProfile();
            }
        });

        btnAddProduct.setOnClickListener(v -> {
            Fragment parent = getParentFragment();
            if (parent instanceof AccountFragment) {
                ((AccountFragment) parent).openAddProduct();
            }
        });

        loadName();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return false;
    }

    void loadName() {
        String fallback = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fallback = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        if (fallback != null && userName != null) {
            userName.setText(fallback);
        }
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(this::bindUser)
                .addOnFailureListener(e -> { /* keep fallback */ });
    }

    void bindUser(DocumentSnapshot snap) {
        if (userName == null) return;
        if (snap != null && snap.exists()) {
            UserDoc doc = snap.toObject(UserDoc.class);
            if (doc != null) {
                String name = (doc.firstName != null ? doc.firstName : "").trim() +
                        " " + (doc.lastName != null ? doc.lastName : "").trim();
                name = name.trim();
                if (!name.isEmpty()) {
                    userName.setText(name);
                }
            }
        }
    }
}
