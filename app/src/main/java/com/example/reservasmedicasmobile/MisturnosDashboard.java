package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public class MisturnosDashboard extends AppCompatActivity {

    private LinearLayout turnosLayout; // Usamos LinearLayout para el contenedor
    private TextView noTurnosMessage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_turnos_dashboard);

        turnosLayout = findViewById(R.id.turno_list); // LinearLayout para mostrar los turnos
        noTurnosMessage = findViewById(R.id.no_turnos_message); // TextView para mostrar el mensaje de 'sin turnos'

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
                    turnosLayout.setVisibility(View.GONE);
                    noTurnosMessage.setVisibility(View.VISIBLE);
                } else {
                    turnosLayout.setVisibility(View.VISIBLE);
                    noTurnosMessage.setVisibility(View.GONE);

                    turnosLayout.removeAllViews(); // Limpiamos las vistas previas
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            String fecha = response.getJSONObject(i).getString("fecha");
                            String medico = response.getJSONObject(i).getString("medico");

                            // Crear un nuevo TextView por cada turno
                            TextView turnoView = new TextView(MisturnosDashboard.this);
                            turnoView.setText("Fecha: " + fecha + ", Médico: " + medico);
                            turnosLayout.addView(turnoView); // Agregarlo al layout
                        } catch (Exception e) {
                            Log.e("MisturnosDashboard", "Error procesando turnos: ", e);
                        }
                    }
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
