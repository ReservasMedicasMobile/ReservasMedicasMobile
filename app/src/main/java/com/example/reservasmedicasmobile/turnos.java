package com.example.reservasmedicasmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class turnos extends AppCompatActivity {

    private Spinner specialtySpinner;
    private Spinner professionalSpinner;
    private Button openDatePickerButton;
    private Button openTimePickerButton;
    private Button timeSlotButton;


    private RequestQueue requestQueue;
    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private Map<String, Map<String, List<String>>> horariosPorEspecialistaYFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (!isLoggedIn) {
            // Redirigir a la pantalla de login si no está logueado
            Intent intent = new Intent(turnos.this, login.class);
            startActivity(intent);
            finish(); // Cierra esta actividad para que no quede en el historial
        } else {
            // El usuario está logueado, continuar cargando la actividad
            setContentView(R.layout.activity_turnos);
            // Tu código para inicializar los elementos de la actividad...
        }




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

        specialtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Especialidad especialidadSeleccionada = (Especialidad) parentView.getItemAtPosition(position);
                int especialidadId = especialidadSeleccionada.getId();

                // Cargar los profesionales que pertenecen a la especialidad seleccionada
                if (especialidadId != 0) {  // Asegurarse de no filtrar si es la opción por defecto
                    cargarProfesionales(especialidadId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No hacer nada si no se selecciona ninguna especialidad
            }
        });

    }

    private void redirectToLogin() {
        Toast.makeText(this, "No estás autenticado. Inicia sesión.", Toast.LENGTH_SHORT).show();
    }





    private void crearTurno() {
        JSONObject turnoData = new JSONObject();
        try {
            // Valores hardcodeados
            int pacienteId = 2; // ID del paciente

            Profesional profesionalSeleccionado = (Profesional) professionalSpinner.getSelectedItem();
            Especialidad especialidadSeleccionada = (Especialidad) specialtySpinner.getSelectedItem();

            int profesionalId = profesionalSeleccionado.getId();
            int especialidadId = especialidadSeleccionada.getId();

            String horaSeleccionada = "10:00"; // Hora hardcodeada
            String fechaSeleccionada = "2024-10-20"; // Fecha hardcodeada (ajusta según tu formato)

            // Validar que los IDs no sean 0 (en caso de que hayas definido una opción por defecto con ID 0)
            if (profesionalId == 0 || especialidadId == 0) {
                Toast.makeText(turnos.this, "Error: profesional o especialidad no válida.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Crear el objeto JSON para el turno
            turnoData.put("paciente", pacienteId);
            turnoData.put("profesional", profesionalId);
            turnoData.put("hora_turno", horaSeleccionada);
            turnoData.put("fecha_turno", fechaSeleccionada);
            turnoData.put("especialidad", especialidadId);

        } catch (JSONException e) {
            Log.e("CrearTurno", "Error al crear el JSON: ", e);
            Toast.makeText(turnos.this, "Error al crear el turno.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Turno", "Creando turno: " + turnoData.toString());

        // Crear la solicitud JSON
        String url = "https://reservasmedicas.ddns.net/api/v1/turnos/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, turnoData,
                response -> {
                    Toast.makeText(turnos.this, "Turno creado exitosamente", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = error.getMessage();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    Toast.makeText(turnos.this, "Error al crear el turno: " + errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Especifica el tipo de contenido
                return headers;
            }
        };

        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        requestQueue.add(jsonObjectRequest);
    }

    public class Especialidad {
        private int id;
        private String nombre;

        public Especialidad(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString() {
            return nombre;  // Esto mostrará el nombre en el Spinner
        }
    }

    public class Profesional {
        private int id;
        private String nombreCompleto;
        private int especialidadId;

        public Profesional(int id, String nombreCompleto, int especialidadId) {
            this.id = id;
            this.nombreCompleto = nombreCompleto;
            this.especialidadId = especialidadId;
        }

        public int getId() {
            return id;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public int getEspecialidadId() {
            return especialidadId;
        }

        @Override
        public String toString() {
            return nombreCompleto;  // Esto mostrará el nombre en el Spinner
        }
    }



    private void cargarEspecialidades() {
        String urlEspecialidades = "https://reservasmedicas.ddns.net/api/v1/especialidad/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlEspecialidades,
                null,
                response -> {
                    ArrayList<Especialidad> especialidadesList = new ArrayList<>();
                    especialidadesList.add(new Especialidad(0, "ESPECIALIDADES MÉDICAS"));  // Opción por defecto

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject especialidad = response.getJSONObject(i);
                            int id = especialidad.getInt("id");
                            String nombre = especialidad.getString("especialidad");
                            especialidadesList.add(new Especialidad(id, nombre));
                            Log.d("Turnos", "Especialidad cargada: " + nombre + " (ID: " + id + ")");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<Especialidad> adapter = new ArrayAdapter<>(turnos.this, R.layout.spinner_item_turnos, especialidadesList);
                    adapter.setDropDownViewResource(R.layout.spinner_item_turnos);
                    specialtySpinner.setAdapter(adapter);
                },
                error -> {
                    Toast.makeText(turnos.this, "Error al cargar especialidades", Toast.LENGTH_SHORT).show();
                    Log.e("Turnos", "Error: " + error.getMessage());
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void cargarProfesionales(int especialidadId) {
        String urlProfesionales = "https://reservasmedicas.ddns.net/api/v1/profesionales/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlProfesionales,
                null,
                response -> {
                    ArrayList<Profesional> profesionalesList = new ArrayList<>();
                    profesionalesList.add(new Profesional(0, "PROFESIONALES", 0));  // Opción por defecto

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject profesional = response.getJSONObject(i);
                            int id = profesional.getInt("id");
                            String nombreCompleto = profesional.getString("nombre") + " " + profesional.getString("apellido");
                            int especialidad = profesional.getInt("especialidad");

                            // Filtrar por especialidad
                            if (especialidad == especialidadId) {
                                profesionalesList.add(new Profesional(id, nombreCompleto, especialidad));
                            }

                            Log.d("Turnos", "Profesional cargado: " + nombreCompleto + " (ID: " + id + ")");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<Profesional> adapter = new ArrayAdapter<>(turnos.this, R.layout.spinner_item_turnos, profesionalesList);
                    adapter.setDropDownViewResource(R.layout.spinner_item_turnos);
                    professionalSpinner.setAdapter(adapter);
                },
                error -> {
                    Toast.makeText(turnos.this, "Error al cargar profesionales", Toast.LENGTH_SHORT).show();
                    Log.e("Turnos", "Error: " + error.getMessage());
                });

        requestQueue.add(jsonArrayRequest);
    }


    private void inicializarHorariosPorEspecialistaYFecha() {
        horariosPorEspecialistaYFecha = new HashMap<>();

        // Definir horarios por especialista y fechas en formato "yyyy-MM-dd"
        Map<String, List<String>> horariosLeandro = new HashMap<>();
        horariosLeandro.put("2024-10-26", Arrays.asList("09:00", "11:00", "12:30"));
        horariosLeandro.put("2024-10-27", Arrays.asList("10:00", "12:00", "14:00"));
        horariosLeandro.put("2024-10-28", Arrays.asList("11:00", "11:30", "13:00"));
        horariosLeandro.put("2024-10-29", Arrays.asList("11:30", "12:00", "13:30"));
        horariosPorEspecialistaYFecha.put("Leandro Martinez", horariosLeandro);

        Map<String, List<String>> horariosCamila = new HashMap<>();
        horariosCamila.put("2024-10-26", Arrays.asList("11:00", "11:30", "12:00", "12:30"));
        horariosCamila.put("2024-10-27", Arrays.asList("10:30", "13:00", "15:00"));
        horariosPorEspecialistaYFecha.put("Camila Medina", horariosCamila);

        Map<String, List<String>> horariosJuan = new HashMap<>();
        horariosJuan.put("2024-10-25", Arrays.asList("14:00", "15:30", "16:00", "17:30"));
        horariosJuan.put("2024-10-26", Arrays.asList("14:30", "15:00", "16:30"));
        horariosPorEspecialistaYFecha.put("Juan Perez Garcia", horariosJuan);

        Map<String, List<String>> horariosNicolas = new HashMap<>();
        horariosNicolas.put("2024-10-25", Arrays.asList("14:00", "15:30", "16:00", "17:30"));
        horariosNicolas.put("2024-10-26", Arrays.asList("14:30", "15:00", "16:30"));
        horariosPorEspecialistaYFecha.put("Nicolás Pérez Ruiz", horariosNicolas);

    }


    private void mostrarFechasDisponibles() {
        String especialistaSeleccionado = professionalSpinner.getSelectedItem().toString();
        if (!horariosPorEspecialistaYFecha.containsKey(especialistaSeleccionado)) {
            Toast.makeText(this, "Selecciona un profesional válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<String>> fechasHorarios = horariosPorEspecialistaYFecha.get(especialistaSeleccionado);
        List<String> fechasDisponibles = new ArrayList<>(fechasHorarios.keySet());

        // Usamos el formato yyyy-MM-dd
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Convertir y ordenar las fechas
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

        // Volver a convertir a cadena después de ordenar
        fechasDisponibles.clear();
        for (Date fechaDate : fechasConvertidas) {
            fechasDisponibles.add(formatoFecha.format(fechaDate));
        }

        // Mostrar las fechas disponibles en un diálogo
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
