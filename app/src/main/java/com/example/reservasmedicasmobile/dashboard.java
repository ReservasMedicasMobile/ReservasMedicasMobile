package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
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

        // Configurar la tarjeta de "Mis Datos" para redirigir a la actividad de pacientes
        findViewById(R.id.card_view_mis_datos).setOnClickListener(v -> goToMisDatos());
    }

    private void showGreeting() {
        // Lógica para mostrar el saludo
        // Ejemplo: greetingTextView.setText("¡Bienvenido!");
    }

    private void goToMisTurnos() {
        Intent intent = new Intent(dashboard.this, MisturnosDashboard.class);
        startActivity(intent);
    }

    private void goToMisDatos() {
        Intent intent = new Intent(dashboard.this, pacientes.class);
        startActivity(intent);
    }
}
