package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public class MisturnosDashboard extends AppCompatActivity {

    private TextView turnosTextView;
    private TextView noTurnosMessage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_turnos_dashboard);

        // Botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        turnosTextView = findViewById(R.id.card_view_misturnos_dashboard);
        noTurnosMessage = findViewById(R.id.no_turnos_message);

        // Obtener el token del SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);

        if (token != null) {
            fetchTurnos(token); // Obtener los turnos
        } else {
            Toast.makeText(this, "Token no disponible. Por favor, inicia sesión.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchTurnos(String token) {
        ApiService apiService = new ApiService(this);
        apiService.fetchTurnos(token, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                if (response.length() == 0) {
                    turnosTextView.setVisibility(View.GONE);
                    noTurnosMessage.setVisibility(View.VISIBLE);
                } else {
                    turnosTextView.setVisibility(View.VISIBLE);
                    noTurnosMessage.setVisibility(View.GONE);
                    StringBuilder turnosBuilder = new StringBuilder();
                    System.out.println(response);

                    // Utilizar la respuesta directamente en lugar de jsonArray
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject turno = response.getJSONObject(i); // Usa response aquí

                            // Extraer los datos necesarios del objeto JSON
                            int paciente = turno.getInt("paciente");
                            int profesional = turno.getInt("profesional");
                            String horaTurno = turno.getString("hora_turno");
                            String fechaTurno = turno.getString("fecha_turno");
                            int especialidad = turno.getInt("especialidad");

                            // Construir la cadena de salida
                            turnosBuilder.append("Paciente: ").append(paciente)
                                    .append(", Profesional: ").append(profesional)
                                    .append(", Hora: ").append(horaTurno)
                                    .append(", Fecha: ").append(fechaTurno)
                                    .append(", Especialidad: ").append(especialidad)
                                    .append("\n");
                        } catch (Exception e) {
                            Log.e("MisturnosDashboard", "Error procesando turnos: ", e);
                        }
                    }

                    // Mostrar los turnos procesados
                    System.out.println(turnosBuilder.toString());
                    turnosTextView.setText(turnosBuilder.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("MisturnosDashboard", "Error al cargar los turnos: ", error);
                Toast.makeText(MisturnosDashboard.this, "Error al cargar los turnos.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
