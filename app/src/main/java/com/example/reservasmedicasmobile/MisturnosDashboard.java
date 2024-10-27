package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MisturnosDashboard extends AppCompatActivity {

    private TextView turnosTextView;
    private TextView noTurnosMessage;
    private ArrayList<TurnoDTO> turnosList; // Lista para almacenar los turnos
    private CardView cardView;
    private RequestQueue rq;
    private LinearLayout linerT;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_turnos_dashboard);



        // Inicializa la lista de turnos
        turnosList = new ArrayList<>();
        linerT = findViewById(R.id.linerT);
        cardView = findViewById(R.id.card_view_misturnos_dashboard);

        // Botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        //turnosTextView = findViewById(R.id.card_view_misturnos_dashboard);
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


    private void fechaTurno(int id) {
        String url = "https://reservasmedicas.ddns.net/lista_turnos_usuario/" + id ;
        //String url = "https://reservasmedicas.ddns.net/api/v1/lista_turnos_usuario/" + 37 ;

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

                String hora = MisTurnos.getString("hora_turno");
                String fecha = MisTurnos.getString("fecha_turno");

                TextView textView = new TextView(this);
                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.append("TIENE UN TURNO \n");
                textView.append("\n");
                textView.append("Hora: " + hora + "\n");
                textView.append("Fecha: " + fecha + "\n");
                textView.append("\n");
                textView.append("*********************************\n");





                linerT.addView(textView);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONError", "Ocurrió una JSONException", e);
            }
        }

       /* try {
            int paciente = MisTurnos.getInt("paciente");
            int profesional = MisTurnos.getInt("profesional");
            String horaTurno = MisTurnos.getString("hora_turno");
            String fechaTurno = MisTurnos.getString("fecha_turno");
            int especialidad = MisTurnos.getInt("especialidad");

            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setPadding(16, 16, 16, 16);
            textView.append("TIENE UN TURNO \n");
            textView.append("\n");
            textView.append("Hora: " + horaTurno + "\n");
            textView.append("Fecha: " + fechaTurno + "\n");
            textView.append("\n");
            textView.append("*********************************\n");
            textView.append("\n");



            // Assuming `cardView` is a reference to your CardView layout
            cardView.addView(textView);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONError", "Ocurrió una JSONException", e);
        }*/
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
