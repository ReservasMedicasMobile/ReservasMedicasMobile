package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.reservasmedicasmobile.modelo.DataModel;

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
                        new AlertDialog.Builder(MisturnosDashboard.this)
                                .setTitle("Información")
                                .setMessage("No tiene turnos reservados")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss(); // Cierra el diálogo al presionar "Aceptar"
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info) // Icono opcional
                                .show();
                        //Toast.makeText(MisturnosDashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                int especialidad = MisTurnos.getInt("especialidad");
                int id = MisTurnos.getInt("id");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView textView = new TextView(this);
                Button deleteButton = new Button(this);
                deleteButton.setTag(id);
                deleteButton.setText("Eliminar Turno");

                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.append("TIENE UN TURNO \n");
                textView.append("\n");
                textView.append("En: "+ convertirEspecialidad(especialidad) +"\n");
                textView.append("Hora: " + hora + "\n");
                textView.append("Fecha: " + fecha + "\n");
                textView.append("\n");
                textView.append("********************     ");




                layout.addView(textView);
                layout.addView(deleteButton);

                deleteButton.setOnClickListener(v -> {
                    int idT = (int) v.getTag();
                    new AlertDialog.Builder(this)
                            .setTitle("Confirmar Eliminación")
                            .setMessage("¿Estás seguro de que deseas eliminar este turno?")
                            .setPositiveButton("Sí", (dialog, which) -> eliminarTurno(idT))
                            .setNegativeButton("No", null)
                            .show();

                });

                linerT.addView(layout);




            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONError", "Ocurrió una JSONException", e);
            }
        }


    }

    private String convertirEspecialidad(int especialidad) {
        switch (especialidad) {
            case 1:
                return "Cardiología";
            case 3:
                return "Traumatologia";
            case 4:
                return "Dermatología";
            case 6:
                return "Pediatria";
            case 7:
                return "Psicologia";
            case 8:
                return "Oncologia";
            case 9:
                return "Psiquiatria";
            case 15:
                return "Ginecologia";
            case 20:
                return "Oftalmologia";
            default:
                return "Especialidad desconocida"; // Valor por defecto si no coincide
        }
    }

    private void eliminarTurno(int id) {
        String url = "https://reservasmedicas.ddns.net/api/v1/turnos/" + id+ "/";

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {

                    Toast.makeText(this, "Turno eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    recreate();

                },
                error -> {

                    Toast.makeText(this, "Error al eliminar el turno: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        rq.add(stringRequest);
    }

}
