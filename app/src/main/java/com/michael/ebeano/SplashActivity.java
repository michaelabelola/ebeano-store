package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.michael.ebeano.services.AuthenticationService;

public class SplashActivity extends AppCompatActivity {
AuthenticationService authService = AuthenticationService.instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                if (authService.isLoggedIn()) {
                    this.startActivity(new Intent(this, MainActivity.class));
                } else {
                    this.startActivity(new Intent(this, LoginActivity.class));
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }
}