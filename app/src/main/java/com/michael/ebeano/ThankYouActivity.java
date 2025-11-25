package com.michael.ebeano;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        Button back = findViewById(R.id.btnBackToProducts);
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductActivity.class));
            finish();
        });
    }
}