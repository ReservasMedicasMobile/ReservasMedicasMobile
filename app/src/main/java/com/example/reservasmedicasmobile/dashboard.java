package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class dashboard extends AppCompatActivity {

    private TextView greetingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Inicializar el TextView para mostrar el saludo
        greetingTextView = findViewById(R.id.greetingTextView);
        showGreeting();

        // Configurar la tarjeta de "Mis Turnos" para redirigir a MisturnosDashboard
        findViewById(R.id.card_view_misturnos_dashboard).setOnClickListener(v -> goToMisTurnos());
    }

    private void showGreeting() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);

        if (token != null) {
            Log.d("TOKEN", "Token: " + token);
            greetingTextView.setText("¡Bienvenido!"); // Personalizar el saludo aquí
        } else {
            Toast.makeText(this, "No se encontró el token", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMisTurnos() {
        Intent intent = new Intent(dashboard.this, MisturnosDashboard.class);
        startActivity(intent);
    }
}
