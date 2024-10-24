package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class dashboard extends AppCompatActivity {

    private TextView greetingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        // Configurar la tarjeta de "Mis Turnos" para redirigir a MisturnosDashboard
        findViewById(R.id.card_view_misturnos_dashboard).setOnClickListener(v -> goToMisTurnos());

        // Configurar la tarjeta de "Mis Datos" para redirigir a la actividad de pacientes
        findViewById(R.id.card_view_mis_datos).setOnClickListener(v -> goToMisDatos());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(dashboard.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_turnos) {
                // Navegar a TurnosActivity
                Intent intent = new Intent(dashboard.this, turnos.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent intent = new Intent(dashboard.this, dashboard.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }

    // Método para redirigir a la pantalla de "Mis Turnos"
    private void goToMisTurnos() {
        Intent intent = new Intent(dashboard.this, pacientes.class);
        startActivity(intent);
    }

    // Método para redirigir a la pantalla de "Mis Datos"
    private void goToMisDatos() {
        Intent intent = new Intent(dashboard.this, MisturnosDashboard.class);
        startActivity(intent);
    }
}


