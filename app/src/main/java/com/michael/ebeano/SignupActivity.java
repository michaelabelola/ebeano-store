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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.michael.ebeano.models.UserDoc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText phone;
    EditText password;
    EditText confirmPassword;
    Button createAccount;
    Button goLogin;
    ProgressBar progress;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        createAccount = findViewById(R.id.btnCreateAccount);
        goLogin = findViewById(R.id.btnGoLogin);
        progress = findViewById(R.id.progress);
        auth = FirebaseAuth.getInstance();

        createAccount.setOnClickListener(v -> doSignup());
        goLogin.setOnClickListener(v -> finish());
    }

    void doSignup() {
        String fn = firstName.getText().toString().trim();
        String ln = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String ph = phone.getText().toString().replaceAll("[^0-9]"," ").trim().replace(" ", "");
        String pw = password.getText().toString();
        String cpw = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(fn)) { firstName.setError(getString(R.string.required)); return; }
        if (TextUtils.isEmpty(ln)) { lastName.setError(getString(R.string.required)); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) { email.setError(getString(R.string.invalid_email)); return; }
        if (ph.length() < 10) { phone.setError(getString(R.string.invalid_phone)); return; }
        if (TextUtils.isEmpty(pw) || pw.length() < 6) { password.setError(getString(R.string.password_min)); return; }
        if (!pw.equals(cpw)) { confirmPassword.setError(getString(R.string.passwords_do_not_match)); return; }

        setLoading(true);
        auth.createUserWithEmailAndPassword(em, pw).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                setLoading(false);
                Toast.makeText(this, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseUser fbUser = auth.getCurrentUser();
            if (fbUser == null) {
                setLoading(false);
                Toast.makeText(this, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = fbUser.getUid();
            UserDoc doc = new UserDoc();
            doc.id = uid;
            doc.email = em;
            doc.passwordHash = sha256(pw);
            doc.firstName = fn;
            doc.lastName = ln;
            doc.phone = ph;
            doc.emailVerified = fbUser.isEmailVerified();
            doc.createdAt = Timestamp.now();
            doc.updatedAt = Timestamp.now();

            FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .set(doc)
                    .addOnSuccessListener(v -> goToProducts())
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(this, getString(R.string.user_save_failed), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    void setLoading(boolean loading) {
        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (createAccount != null) createAccount.setEnabled(!loading);
        if (goLogin != null) goLogin.setEnabled(!loading);
    }

    void goToProducts() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return input; // fallback (should not happen)
        }
    }
}
