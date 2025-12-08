package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    Button login;
    Button signup;
    ProgressBar progress;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        login = findViewById(R.id.btnLogin);
        signup = findViewById(R.id.btnSignup);
        progress = findViewById(R.id.progress);
        auth = FirebaseAuth.getInstance();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) goToProducts();
        View.OnClickListener handler = v -> {
            String e = email.getText().toString().trim();
            String p = password.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                email.setError("Invalid email");
                return;
            }
            if (TextUtils.isEmpty(p) || p.length() < 6) {
                password.setError("Min 6 chars");
                return;
            }
            progress.setVisibility(View.VISIBLE);
            if (v.getId() == R.id.btnSignup) {
                auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(task -> {
                    progress.setVisibility(View.GONE);
                    if (task.isSuccessful()) goToProducts(); else Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show();
                });
            } else {
                auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(task -> {
                    progress.setVisibility(View.GONE);
                    if (task.isSuccessful()) goToProducts(); else Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                });
            }
        };
        login.setOnClickListener(handler);
        signup.setOnClickListener(handler);
    }

    void goToProducts() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}