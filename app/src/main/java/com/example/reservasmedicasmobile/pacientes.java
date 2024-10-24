package com.example.reservasmedicasmobile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class pacientes extends AppCompatActivity {

    private Spinner spinner_obraSocial;
    private EditText etNombreCompleto, etApellido, etDni, etCorreo, etTelefono;
    private Button btnSubmits, buttonOpenDatePickers;
    private ImageButton imageperfil;
    private CalendarView calendarView;
    private String selectedDate;



    private RequestQueue requestQueue;
    private String fechaSeleccionada = "";
    private Map<String, Map<String, List<String>>> horariosPorEspecialistaYFecha;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pacientes);


        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);




        if (!isLoggedIn) {

            Intent intent = new Intent(pacientes.this, login.class);
            startActivity(intent);
            finish();
        } else {

            setContentView(R.layout.activity_pacientes);

        }

        calendarView = findViewById(R.id.calendar_view);
        spinner_obraSocial = findViewById(R.id.spinner_obraSocial);
        etNombreCompleto = findViewById(R.id.etNombreCompleto);
        etApellido = findViewById(R.id.etApellido);
        etDni = findViewById(R.id.etDni);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        btnSubmits = findViewById(R.id.btnSubmits);
        buttonOpenDatePickers = findViewById(R.id.button_open_date_pickers);

        //Volley
        requestQueue = Volley.newRequestQueue(this);

        cargarObraSocial();
        calendarView.setVisibility(View.GONE);
        buttonOpenDatePickers.setOnClickListener(v -> {
            if (calendarView.getVisibility() == View.GONE) {
                calendarView.setVisibility(View.VISIBLE);
            } else {
                calendarView.setVisibility(View.GONE);
            }


        });
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // Formato: YYYY-MM-DD
            Log.d("TAG", "Fecha seleccionada: " + selectedDate);

        });






        btnSubmits.setOnClickListener(v -> {

            agregarDatosPacientes();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(pacientes.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_turnos) {
                // Navegar a TurnosActivity
                Intent intent = new Intent(pacientes.this, turnos.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent intent = new Intent(pacientes.this, dashboard.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }



    //---------------Datos Paciente---------------
    private void agregarDatosPacientes() {

        int userId = getUserId();
        JSONObject datosPaciente = new JSONObject();
        try {


            String nombre = etNombreCompleto.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String dni = etDni.getText().toString().trim();
            String date = selectedDate;
            String correo = etCorreo.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            ObraSocial obraSocialSeleccionado = (ObraSocial) spinner_obraSocial.getSelectedItem();
            int obraSocialId = obraSocialSeleccionado.getId();

            if (obraSocialId == 0) {
                Toast.makeText(pacientes.this, "Error: Selecione una Obra Social.", Toast.LENGTH_SHORT).show();
                return;
            }
            datosPaciente.put("id", userId);
            datosPaciente.put("nombre", nombre);
            datosPaciente.put("apellido", apellido);
            datosPaciente.put("dni", dni);
            datosPaciente.put("fecha_nacimiento", date);
            datosPaciente.put("correo", correo);
            datosPaciente.put("telefono", telefono);
            datosPaciente.put("obra_social", obraSocialId);


        } catch (JSONException e) {
            Log.e("CrearPacientes", "Error al crear el JSON: ", e);
            Toast.makeText(pacientes.this, "Error al crear el Paciente.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Datos Personales", "Creando Correctamente: " + datosPaciente.toString());

        String url = "https://reservasmedicas.ddns.net/api/v1/paciente/";
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

        Intent intent = new Intent(pacientes.this, dashboard.class);
        startActivity(intent);
    }


    //-------------- Obra Social----------------
    public class ObraSocial {
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

    private void cargarObraSocial() {

        String urlObraSocial = "https://reservasmedicas.ddns.net/api/v1/obra_social/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlObraSocial,
                null,
                response -> {
                    ArrayList<ObraSocial> obraSocialsList = new ArrayList<>();
                    obraSocialsList.add(new ObraSocial(0, "OBRA SOCIAL"));

                    for (int i = 0; i < response.length(); i++) {
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

                }, error -> {
            Toast.makeText(pacientes.this, "Error al cargar obra social", Toast.LENGTH_SHORT).show();
            Log.e("Obra Social", "Error: " + error.getMessage());
        }
        );
        requestQueue.add(jsonArrayRequest);
    }


    public int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1); //
    }


}