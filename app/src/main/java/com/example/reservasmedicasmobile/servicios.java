package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.reservasmedicasmobile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class servicios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);

        Button reserveButton = findViewById(R.id.reserve_button);

        reserveButton.setOnClickListener(v -> {
            Intent intent = new Intent(servicios.this, turnos.class);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(servicios.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_login) {
                // Navegar a TurnosActivity
                Intent intent = new Intent(servicios.this, login.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_servicios) {
                Intent intent = new Intent(servicios.this, servicios.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }
}