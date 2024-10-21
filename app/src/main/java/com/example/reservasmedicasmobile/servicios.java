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
                Toast.makeText(servicios.this, "Inicio seleccionado", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_turnos) {
                // Navegar a TurnosActivity
                Intent intent = new Intent(servicios.this, turnos.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Toast.makeText(servicios.this, "Perfil seleccionado", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });

        ImageButton backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {

            finish();
        });

    }
}