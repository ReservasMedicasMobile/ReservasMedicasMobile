package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.reservasmedicasmobile.modelo.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.http.DELETE;

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
                String especialidad = MisTurnos.getString("nombre_especialidad");
                String profesional = MisTurnos.getString("nombre_profesional");
                int id = MisTurnos.getInt("id");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView textView = new TextView(this);
                Button deleteButton = new Button(this);
                Button PagarButton = new Button(this);

                PagarButton.setTag(id);
                PagarButton.setText("Pagar Turno");
                PagarButton.setBackgroundColor(Color.parseColor("#151635"));
                PagarButton.setTextColor(Color.WHITE);
                PagarButton.setTextSize(16);
                PagarButton.setPadding(16, 16, 16, 16);

                deleteButton.setTag(id);
                deleteButton.setText("Cancelar Turno");
                deleteButton.setBackgroundColor(Color.parseColor("#D10000"));
                deleteButton.setTextColor(Color.WHITE);
                deleteButton.setTextSize(16);
                deleteButton.setPadding(16, 16, 16, 16); // Padding

                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.append("TIENE UN TURNO \n");
                textView.append("\n");
                textView.append("En: "+ especialidad +"\n");
                textView.append("Con el Medico/a: "+ profesional +"\n");
                textView.append("Hora: " + hora + "\n");
                textView.append("Fecha: " + fecha + "\n");
                textView.append("\n");






                layout.addView(textView);
                layout.addView(deleteButton);
                layout.addView(PagarButton);

                deleteButton.setOnClickListener(v -> {
                    int idT = (int) v.getTag();
                    new AlertDialog.Builder(this)
                            .setTitle("Confirmar Cancelación")
                            .setMessage("¿Estás seguro de que deseas cancelar este turno?")
                            .setPositiveButton("Sí", (dialog, which) -> eliminarTurno(idT))
                            .setNegativeButton("No", null)
                            .show();

                });

                PagarButton.setOnClickListener(v -> {
                    int idTurno = (int) v.getTag();

                    JSONArray items = new JSONArray();
                    JSONObject item = new JSONObject();

                    try {
                        item.put("title", "Turno médico con " + profesional);
                        item.put("image", "https://via.placeholder.com/150");
                        item.put("price", 5000); // en centavos = $50.00
                        items.put(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("items", items);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = "http://10.0.2.2:4242/checkout"; // usa la IP correspondiente

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                            response -> {
                                try {
                                    String stripeUrl = response.getString("url");
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(stripeUrl));
                                    startActivity(browserIntent); // Esto abre el navegador y lleva al pago
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {
                                error.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error iniciando pago", Toast.LENGTH_SHORT).show();
                            });

                    queue.add(request);
                });



                linerT.addView(layout);




            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSONError", "Ocurrió una JSONException", e);
            }
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
