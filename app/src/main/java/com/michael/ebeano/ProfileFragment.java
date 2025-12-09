package com.michael.ebeano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.michael.ebeano.models.UserDoc;

public class ProfileFragment extends Fragment {

    MaterialToolbar toolbar;
    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbarProfile);
        if (toolbar != null) {
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        loadUser();
    }

    void loadUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(this::bind)
                .addOnFailureListener(e -> bind(null));
    }

    void bind(@Nullable DocumentSnapshot snap) {
        UserDoc doc = null;
        if (snap != null && snap.exists()) doc = snap.toObject(UserDoc.class);
        if (doc != null) {
            if (firstName != null) firstName.setText(nullToEmpty(doc.firstName));
            if (lastName != null) lastName.setText(nullToEmpty(doc.lastName));
            if (email != null) email.setText(nullToEmpty(doc.email));
            if (phone != null) phone.setText(nullToEmpty(doc.phone));
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (email != null) email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    static String nullToEmpty(String s) { return s == null ? "" : s; }
}
