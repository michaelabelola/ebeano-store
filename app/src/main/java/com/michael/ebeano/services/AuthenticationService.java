package com.michael.ebeano.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

public class AuthenticationService {
    public static final AuthenticationService instance = new AuthenticationService();
    private FirebaseAuth auth;

    private AuthenticationService() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getAuth() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return getAuth() != null;
    }
}
