package com.michael.ebeano;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    TextView displayName;
    TextView email;
    TextView phone;
    TextView avatarInitials;
    ProgressBar progress;

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
        displayName = view.findViewById(R.id.displayName);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        avatarInitials = view.findViewById(R.id.avatarInitials);
        progress = view.findViewById(R.id.progressProfile);

        // Fallbacks before loading
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String em = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (email != null && em != null) email.setText(em);
            if (displayName != null) displayName.setText(displayFromEmail(em));
            if (avatarInitials != null) avatarInitials.setText(initialsFrom(displayName != null ? displayName.getText().toString() : em));
        }
        loadUser();
    }

    void loadUser() {
        if (progress != null) progress.setVisibility(View.VISIBLE);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (progress != null) progress.setVisibility(View.GONE);
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(this::bind)
                .addOnFailureListener(e -> bind(null));
    }

    void bind(@Nullable DocumentSnapshot snap) {
        if (progress != null) progress.setVisibility(View.GONE);
        UserDoc doc = null;
        if (snap != null && snap.exists()) doc = snap.toObject(UserDoc.class);
        String em = null;
        if (doc != null) em = nullToEmpty(doc.email);
        if (em == null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            em = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        String name = doc != null ? (nullToEmpty(doc.firstName) + " " + nullToEmpty(doc.lastName)).trim() : null;
        if (name == null || name.isEmpty()) name = displayFromEmail(em);

        if (displayName != null) displayName.setText(name);
        if (email != null) email.setText(em != null ? em : "");

        String ph = doc != null ? nullToEmpty(doc.phone) : "";
        if (ph.isEmpty()) {
            if (phone != null) phone.setVisibility(View.GONE);
        } else {
            if (phone != null) {
                phone.setVisibility(View.VISIBLE);
                phone.setText(formatPhone(ph));
            }
        }

        if (avatarInitials != null) avatarInitials.setText(initialsFrom(name != null ? name : em));
    }

    static String nullToEmpty(String s) { return s == null ? "" : s; }

    static String displayFromEmail(@Nullable String email) {
        if (email == null) return "";
        int at = email.indexOf('@');
        if (at > 0) return email.substring(0, at);
        return email;
    }

    static String initialsFrom(@Nullable String source) {
        if (source == null || source.trim().isEmpty()) return "?";
        String[] parts = source.trim().split("\\s+");
        if (parts.length >= 2) {
            char a = Character.toUpperCase(parts[0].charAt(0));
            char b = Character.toUpperCase(parts[1].charAt(0));
            return new String(new char[]{a, b});
        }
        char a = Character.toUpperCase(parts[0].charAt(0));
        return String.valueOf(a);
    }

    static String formatPhone(@NonNull String input) {
        String digits = input.replaceAll("[^0-9]", "");
        if (digits.length() == 10) {
            return String.format("(%s) %s-%s", digits.substring(0,3), digits.substring(3,6), digits.substring(6));
        }
        return input;
    }
}
