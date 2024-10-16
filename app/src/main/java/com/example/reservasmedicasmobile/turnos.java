package com.example.reservasmedicasmobile;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.bottomnavigation.BottomNavigationView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class turnos extends AppCompatActivity {

    private Spinner specialtySpinner;
    private Spinner professionalSpinner;
    private Button openDatePickerButton;
    private Button openTimePickerButton;
    private Button timeSlotButton;


    private ArrayList<Paciente> pacientesList;
    private RequestQueue requestQueue;
    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private Map<String, Map<String, List<String>>> horariosPorEspecialistaYFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);


        specialtySpinner = findViewById(R.id.spinner_specialty);
        professionalSpinner = findViewById(R.id.spinner_professional);
        openDatePickerButton = findViewById(R.id.button_open_date_picker);
        openTimePickerButton = findViewById(R.id.button_open_time_picker);
        timeSlotButton = findViewById(R.id.button_time_slot);


        // Inicializar Volley
        requestQueue = Volley.newRequestQueue(this);


        requestQueue = Volley.newRequestQueue(this);
        inicializarHorariosPorEspecialistaYFecha();

        cargarEspecialidades();
        cargarProfesionales();
        cargarPacientes();

        openDatePickerButton.setOnClickListener(v -> mostrarFechasDisponibles());

        // Configurar el DatePickerDialog
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                fechaSeleccionada = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                Toast.makeText(this, "Fecha seleccionada: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();
            }, year, month, day);

        professionalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
         }
        });

        openTimePickerButton.setOnClickListener(v -> {
            mostrarHorariosDisponibles();
        });

        timeSlotButton.setOnClickListener(v -> {
            crearTurno();
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(turnos.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_turnos) {
                // Navegar a TurnosActivity
                Intent intent = new Intent(turnos.this, turnos.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent intent = new Intent(turnos.this, dashboard.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

    }
    private class Paciente {
        private int id;
        private String nombre;
        private String apellido;

        // Constructor
        public Paciente(int id, String nombre, String apellido) {
            this.id = id;
            this.nombre = nombre;
            this.apellido = apellido;
        }

        // Getters
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }

        // Método para obtener el nombre completo
        public String getNombreCompleto() {
            return nombre + " " + apellido;
        }
    }


    private void inicializarHorariosPorEspecialistaYFecha() {
        horariosPorEspecialistaYFecha = new HashMap<>();

        // Definir horarios por especialista y varias fechas
        Map<String, List<String>> horariosLeandro = new HashMap<>();
        horariosLeandro.put("10/10/2024", Arrays.asList("09:00", "11:00", "12:30"));
        horariosLeandro.put("11/10/2024", Arrays.asList("10:00", "12:00", "14:00"));
        horariosLeandro.put("14/10/2024", Arrays.asList("11:00", "11:30", "13:00"));
        horariosLeandro.put("15/10/2024", Arrays.asList("11:30", "12:00", "13:30"));
        horariosPorEspecialistaYFecha.put("Leandro Martinez", horariosLeandro);

        Map<String, List<String>> horariosCamila = new HashMap<>();
        horariosCamila.put("10/10/2024", Arrays.asList("11:00", "11:30", "12:00", "12:30"));
        horariosCamila.put("11/10/2024", Arrays.asList("10:30", "13:00", "15:00"));
        horariosPorEspecialistaYFecha.put("Camila Pérez Ruiz", horariosCamila);

        Map<String, List<String>> horariosNicolas = new HashMap<>();
        horariosNicolas.put("10/10/2024", Arrays.asList("14:00", "15:30", "16:00", "17:30"));
        horariosNicolas.put("11/10/2024", Arrays.asList("14:30", "15:00", "16:30"));
        horariosPorEspecialistaYFecha.put("Nicolás Medina", horariosNicolas);
    }
    private void cargarPacientes() {
        String urlPacientes = "https://reservasmedicas.ddns.net/api/v1/paciente/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlPacientes,
                null,
                response -> {
                    // Almacenar pacientes en una lista interna
                    pacientesList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject paciente = response.getJSONObject(i);
                            int id = paciente.getInt("id"); // Suponiendo que hay un campo "id"
                            String nombre = paciente.getString("nombre");
                            String apellido = paciente.getString("apellido");
                            Paciente nuevoPaciente = new Paciente(id, nombre, apellido);
                            pacientesList.add(nuevoPaciente);
                            Log.d("Turnos", "Paciente cargado: " + nuevoPaciente.getNombreCompleto());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    Log.e("Turnos", "Error al cargar pacientes: " + error.getMessage());
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void cargarEspecialidades() {
        String urlEspecialidades = "https://reservasmedicas.ddns.net/api/v1/especialidad/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlEspecialidades,
                null,
                response -> {
                    ArrayList<String> especialidadesList = new ArrayList<>();
                    especialidadesList.add("ESPECIALIDADES MÉDICAS");

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject especialidad = response.getJSONObject(i);
                            String especialidadCompleta = especialidad.getString("especialidad");
                            especialidadesList.add(especialidadCompleta);
                            Log.d("Turnos", "Especialidad cargada: " + especialidadCompleta);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(turnos.this, R.layout.spinner_item_turnos, especialidadesList);
                    adapter.setDropDownViewResource(R.layout.spinner_item_turnos);
                    specialtySpinner.setAdapter(adapter);
                },
                error -> {
                    Toast.makeText(turnos.this, "Error al cargar especialidades", Toast.LENGTH_SHORT).show();
                    Log.e("Turnos", "Error: " + error.getMessage());
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void cargarProfesionales() {
        String urlProfesionales = "https://reservasmedicas.ddns.net/api/v1/profesionales/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlProfesionales,
                null,
                response -> {
                    ArrayList<String> profesionalesList = new ArrayList<>();
                    profesionalesList.add("PROFESIONALES");

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject profesional = response.getJSONObject(i);
                            String nombreCompleto = profesional.getString("nombre") + " " + profesional.getString("apellido");
                            profesionalesList.add(nombreCompleto);
                            Log.d("Turnos", "Profesional cargado: " + nombreCompleto);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(turnos.this, R.layout.spinner_item_turnos, profesionalesList);
                    adapter.setDropDownViewResource(R.layout.spinner_item_turnos);
                    professionalSpinner.setAdapter(adapter);
                },
                error -> {
                    Toast.makeText(turnos.this, "Error al cargar profesionales", Toast.LENGTH_SHORT).show();
                    Log.e("Turnos", "Error: " + error.getMessage());
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void crearTurno() {
        String urlCrearTurno = "https://reservasmedicas.ddns.net/api/v1/turnos/";
        int pacienteId = 1; // Cambiar a un paciente hardcodeado para pruebas

        JSONObject turnoData = new JSONObject();
        try {
            String especialidadSeleccionada = specialtySpinner.getSelectedItem().toString();
            String profesionalSeleccionado = professionalSpinner.getSelectedItem().toString();

            if ("ESPECIALIDADES MÉDICAS".equals(especialidadSeleccionada) ||
                    "PROFESIONALES".equals(profesionalSeleccionado) ||
                    horaSeleccionada.isEmpty() ||
                    fechaSeleccionada.isEmpty()) {
                Toast.makeText(turnos.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            turnoData.put("paciente", pacienteId);
            turnoData.put("especialidad", especialidadSeleccionada);
            turnoData.put("profesional", profesionalSeleccionado);
            turnoData.put("fecha", fechaSeleccionada);
            turnoData.put("hora", horaSeleccionada);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Turnos", "Datos del turno: " + turnoData.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                urlCrearTurno,
                turnoData,
                response -> Toast.makeText(turnos.this, "Turno creado exitosamente", Toast.LENGTH_SHORT).show(),
                error -> {
                    String errorMessage = "Error al crear turno: " + error.getMessage();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorBody = new String(error.networkResponse.data);
                        errorMessage += "\nDetalles: " + errorBody;
                    }
                    Toast.makeText(turnos.this, errorMessage, Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }


    private void mostrarFechasDisponibles() {
        String especialistaSeleccionado = professionalSpinner.getSelectedItem().toString();
        if (!horariosPorEspecialistaYFecha.containsKey(especialistaSeleccionado)) {
            Toast.makeText(this, "Selecciona un profesional válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<String>> fechasHorarios = horariosPorEspecialistaYFecha.get(especialistaSeleccionado);
        List<String> fechasDisponibles = new ArrayList<>(fechasHorarios.keySet());

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        List<Date> fechasConvertidas = new ArrayList<>();
        for (String fecha : fechasDisponibles) {
            try {
                Date fechaDate = formatoFecha.parse(fecha);
                fechasConvertidas.add(fechaDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(fechasConvertidas);

        fechasDisponibles.clear();
        for (Date fechaDate : fechasConvertidas) {
            fechasDisponibles.add(formatoFecha.format(fechaDate));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, fechasDisponibles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            fechaSeleccionada = fechasDisponibles.get(position);
            openDatePickerButton.setText("Fecha: " + fechaSeleccionada);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Seleccionar", (dialog, which) -> {
            Toast.makeText(turnos.this, "Fecha seleccionada: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarHorariosDisponibles() {
        String especialistaSeleccionado = professionalSpinner.getSelectedItem().toString();
        if (!horariosPorEspecialistaYFecha.containsKey(especialistaSeleccionado)) {
            Toast.makeText(this, "Selecciona un profesional válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha primero", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<String>> fechasHorarios = horariosPorEspecialistaYFecha.get(especialistaSeleccionado);
        List<String> horariosDisponibles = fechasHorarios.get(fechaSeleccionada);

        if (horariosDisponibles == null || horariosDisponibles.isEmpty()) {
            Toast.makeText(this, "No hay horarios disponibles para la fecha seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, horariosDisponibles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            horaSeleccionada = horariosDisponibles.get(position);
            openTimePickerButton.setText("Horario: " + horaSeleccionada);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Seleccionar", (dialog, which) -> {
            Toast.makeText(turnos.this, "Hora seleccionada: " + horaSeleccionada, Toast.LENGTH_SHORT).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
