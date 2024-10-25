package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.reservasmedicasmobile.modelo.DataModelTurnos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MisturnosDashboard extends AppCompatActivity {

    private TextView turnosTextView;
    private TextView noTurnosMessage;
    private ArrayList<TurnoDTO> turnosList; // Lista para almacenar los turnos
    private RequestQueue rq;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_turnos_dashboard);



        // Inicializa la lista de turnos
        turnosList = new ArrayList<>();

        // Botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        turnosTextView = findViewById(R.id.card_view_misturnos_dashboard);
        noTurnosMessage = findViewById(R.id.no_turnos_message);

        // Obtener el token del SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);
        int id =  sharedPreferences.getInt("id", -1);

        /*if (token != null) {
            fetchTurnos(token); // Obtener los turnos
        } else {
            Toast.makeText(this, "Token no disponible. Por favor, inicia sesión.", Toast.LENGTH_SHORT).show();
        }*/

        rq = Volley.newRequestQueue(this);


        fechaTurno(id);
        System.out.println();

    }

    public void fechaTurno(int id){

        String url = "https://reservasmedicas.ddns.net/api/v1/turnos/" + id ;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mostrarDatos(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MisturnosDashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        rq.add(jsonArrayRequest);

    }

    public void mostrarDatos(JSONArray DataModelTurnos) {
        for (int i = 0; i < DataModelTurnos.length(); i++) {
            try {
                JSONObject MisTurnos = DataModelTurnos.getJSONObject(i);
                int paciente = MisTurnos.getInt("paciente");
                int profesional = MisTurnos.getInt("profesional");
                String horaTurno = MisTurnos.getString("hora_turno");
                String fechaTurno = MisTurnos.getString("fecha_turno");
                int especialidad = MisTurnos.getInt("especialidad");

                TextView textView = new TextView(this);
                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.setText(paciente + ": \n");
                textView.append(profesional + ": \n");
                textView.append("* "+horaTurno + "\n");
                textView.append("* "+fechaTurno + "\n");
                textView.append("* "+especialidad + "\n");
                textView.append("----------------------------------------------------------\n");


            } catch (JSONException e) {

                Log.e("JSONError", "Ocurrió una JSONException", e);



            }

        }
    }
   /* private void fetchTurnos(String token) {
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

                    // Utilizar la respuesta directamente en lugar de jsonArray
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject turno = response.getJSONObject(i);

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

                    // Mostrar los turnos procesados en el cuerpo de la página
                    turnosTextView.setText(turnosBuilder.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("MisturnosDashboard", "Error al cargar los turnos: ", error);
                Toast.makeText(MisturnosDashboard.this, "Error al cargar los turnos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/






}
