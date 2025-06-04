package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class dashboard extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView saludoTextView;
    private TextView turnoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        saludoTextView = findViewById(R.id.greeting_text);
        turnoTextView = findViewById(R.id.recordatorio_text);
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Mostrar saludo personalizado
        String nombre = prefs.getString("first_name", "Usuario");
        saludoTextView.setText("Hola, " + nombre);

        // Mostrar turno próximo
        mostrarProximoTurno();

        // Navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(dashboard.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_turnos) {
                startActivity(new Intent(dashboard.this, turnos.class));
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                startActivity(new Intent(dashboard.this, dashboard.class));
                return true;
            } else {
                return false;
            }
        });

        // Acciones de las tarjetas
        findViewById(R.id.card_view_misturnos_dashboard).setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, MisturnosDashboard.class);
            startActivity(intent);
        });

        findViewById(R.id.card_view_mis_datos).setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, pacientes.class);
            startActivity(intent);
        });
    }

    private void mostrarProximoTurno() {
        String turnosJson = prefs.getString("turnos_usuario", null);

        if (turnosJson == null || turnosJson.isEmpty()) {
            turnoTextView.setText("Actualmente no tenés turnos programados.");
            return;
        }

        try {
            JSONArray turnos = new JSONArray(turnosJson);
            SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdfMostrar = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date ahora = new Date();
            StringBuilder sb = new StringBuilder();
            int cantidad = 0;

            for (int i = 0; i < turnos.length(); i++) {
                JSONObject turno = turnos.getJSONObject(i);
                String fecha = turno.optString("fecha", "");
                String hora = turno.optString("hora", "");
                String especialidad = turno.optString("especialidad", "Desconocida");

                if (fecha.isEmpty() || hora.isEmpty()) continue;

                Date fechaCompleta = sdfFull.parse(fecha + " " + hora);
                if (fechaCompleta != null && fechaCompleta.after(ahora)) {
                    String fechaFormateada = sdfMostrar.format(fechaCompleta);
                    sb.append("• ").append(fechaFormateada)
                            .append(" a las ").append(hora)
                            .append(" hs. - ").append(especialidad)
                            .append("\n");
                    cantidad++;
                }
            }

            if (cantidad > 0) {
                turnoTextView.setText("TUS PRÓXIMOS TURNOS:\n\n" + sb.toString().trim());
            } else {
                prefs.edit().remove("turnos_usuario").apply();
                turnoTextView.setText("Actualmente no tenés turnos programados.");
            }

        } catch (Exception e) {
            turnoTextView.setText("Ocurrió un error al cargar los turnos.");
            e.printStackTrace();
        }
    }
}
