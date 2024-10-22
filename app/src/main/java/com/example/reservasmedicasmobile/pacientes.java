package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class pacientes extends AppCompatActivity {

    private Spinner spinner_obraSocial;
    private EditText etNombreCompleto, etApellido, etDni, etCorreo, etTelefono;
    private Button btnSubmits, button_open_date_pickers;
    private ImageButton imageperfil;



    private RequestQueue requestQueue;
    private String fechaSeleccionada = "";
    private Map<String, Map<String, List<String>>> horariosPorEspecialistaYFecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pacientes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int userId;
        userId = sharedPreferences.getInt();
        String token = sharedPreferences.getString("token", null);
        editor.putInt("id", userId); // `userId` es el ID del usuario logueado
        editor.apply();






        if (token != null) {
            userId = getUserIdFromToken(token); // Extrae el ID del usuario
        }


        if (token != null) {
            userId = getUserIdFromToken(token); // Extrae el ID del usuario
        }




        spinner_obraSocial = findViewById(R.id.spinner_obraSocial);
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etApellido = findViewById(R.id.etApellido);
        etDni = findViewById(R.id.etDni);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        btnSubmits = findViewById(R.id.btnSubmits);
        button_open_date_pickers = findViewById(R.id.button_open_date_pickers);

        //Volley
        requestQueue = Volley.newRequestQueue(this);

        cargarObraSocial();
        int finalUserId = userId;
        btnSubmits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregaarDatosPacientes(finalUserId);
            }
        });

    }

//---------------Datos Paciente---------------
    private void agregaarDatosPacientes(int userId){
        JSONObject datosPaciente = new JSONObject();
        try {
            ObraSocial obraSocialSeleccionado = (ObraSocial) spinner_obraSocial.getSelectedItem();
            int obraSocialId = obraSocialSeleccionado.getId();

            if (obraSocialId == 0) {
                Toast.makeText(pacientes.this, "Error: Selecione una Obra Social.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                datosPaciente.put("user_id", userId); // Añadir el ID al JSON
            } catch (JSONException e) {
                e.printStackTrace();
            }
            datosPaciente.put("nombre", etNombreCompleto);
            datosPaciente.put("apellido", etApellido);
            datosPaciente.put("dni", etDni);
            datosPaciente.put("fecha_nacimiento", button_open_date_pickers);
            datosPaciente.put("correo", etCorreo);
            datosPaciente.put("telefono", etTelefono);
            datosPaciente.put("obra_social", obraSocialId);
            datosPaciente.put("user_id", userId);
        }catch (JSONException e){
            Log.e("CrearPacientes", "Error al crear el JSON: ", e);
            Toast.makeText(pacientes.this, "Error al crear el Paciente.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Datos Personales", "Creando Correctamente: " + datosPaciente.toString());

        String url = "https://reservasmedicas.ddns.net/api/v1/paciente/"+ userId +"/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, datosPaciente,
                response -> {
                    Toast.makeText(pacientes.this, "Datos cargados exitosamente", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = error.getMessage();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    Toast.makeText(pacientes.this, "Error al crear el turno: " + errorMessage, Toast.LENGTH_SHORT).show();
                });


        requestQueue.add(jsonObjectRequest);
    }
//-------------- Obra Social----------------
    public class ObraSocial{
        private int id;
        private String nombre_obra;

        public ObraSocial(int id, String nombre_obra) {
            this.id = id;
            this.nombre_obra = nombre_obra;
        }

        public int getId() {
            return id;
        }

        public String getNombre_obra() {
            return nombre_obra;
        }

        @Override
        public String toString() {
            return nombre_obra;
        }
    }

    private void cargarObraSocial(){

        String urlObraSocial = "https://reservasmedicas.ddns.net/api/v1/obra_social/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlObraSocial,
                null,
                response -> {
                    ArrayList<ObraSocial> obraSocialsList = new ArrayList<>();
                    obraSocialsList.add(new ObraSocial(0, "OBRA SOCIAL"));

                    for (int i=0; i < response.length(); i++){
                        try {
                            JSONObject obraSocial = response.getJSONObject(i);
                            int id = obraSocial.getInt("id");
                            String nombre_obra = obraSocial.getString("nombre_obra");
                            obraSocialsList.add(new ObraSocial(id, nombre_obra));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<ObraSocial> adapter = new ArrayAdapter<>(pacientes.this, R.layout.spinner_item_obrasocial, obraSocialsList);
                    adapter.setDropDownViewResource(R.layout.spinner_item_obrasocial);
                    spinner_obraSocial.setAdapter(adapter);

                },error -> {
                Toast.makeText(pacientes.this, "Error al cargar obra social", Toast.LENGTH_SHORT).show();
                Log.e("Obra Social", "Error: " + error.getMessage());
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

//-----------Recuperar Id Token--------------------
public int getUserIdFromToken(String token) {
    String[] parts = token.split("\\.");
    if (parts.length > 1) {
        String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
        try {
            JSONObject json = new JSONObject(payload);
            return json.getInt("user_id"); // Asegúrate de que el nombre del campo sea correcto
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    return -1; // Valor por defecto si no se puede extraer el ID
}
}
