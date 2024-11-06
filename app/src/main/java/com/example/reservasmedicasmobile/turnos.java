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
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        int id =  sharedPreferences.getInt("id", -1);

        if (!isLoggedIn) {
            // Redirigir a la pantalla de login si no está logueado
            Intent intent = new Intent(turnos.this, login.class);
            startActivity(intent);
            finish();
        } else {
            // El usuario está logueado, continuar cargando la actividad
            setContentView(R.layout.activity_turnos);
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
        obtenerPacientes();

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

    public class Paciente {
        private int id;
        private String nombre;
        public Paciente(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        public int getId() {
            return id;
        }
        public String getNombre() {
            return nombre;
        }
    }
    private List<Paciente> listaPacientes = new ArrayList<>(); // Lista para almacenar pacientes
    private void obtenerPacientes() {
        String url = "https://reservasmedicas.ddns.net/api/v1/paciente/"; // URL de la API para obtener pacientes
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Limpiar la lista antes de agregar nuevos pacientes
                        listaPacientes.clear();
                        // Recorrer la respuesta JSON y agregar pacientes a la lista
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject pacienteJson = response.getJSONObject(i);
                            int id = pacienteJson.getInt("id");
                            String nombre = pacienteJson.getString("nombre");
                            // Crea un objeto Paciente y añádelo a la lista
                            listaPacientes.add(new Paciente(id, nombre));
                        }
                    } catch (JSONException e) {
                        Log.e("ObtenerPacientes", "Error al parsear la respuesta: ", e);
                    }
                },
                error -> {
                    Toast.makeText(turnos.this, "Error al obtener pacientes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
        // Agregar la solicitud a la cola
        requestQueue.add(jsonArrayRequest);
    }

    public int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1);
    }

    private void crearTurno() {
        JSONObject turnoData = new JSONObject();
        try {
            // Validar que haya pacientes disponibles
            if (listaPacientes.isEmpty()) {
                Toast.makeText(turnos.this, "No hay pacientes disponibles.", Toast.LENGTH_SHORT).show();
                return;
            }


            // Seleccionar un paciente aleatorio
            Paciente pacienteAleatorio = listaPacientes.get(new Random().nextInt(listaPacientes.size()));
            int pacienteId = pacienteAleatorio.getId(); // ID del paciente aleatorio

            // Obtener el profesional seleccionado del Spinner
            Profesional profesionalSeleccionado = (Profesional) professionalSpinner.getSelectedItem();

            // Verificar si el profesional seleccionado es nulo
            if (profesionalSeleccionado == null) {
                Toast.makeText(turnos.this, "Por favor, selecciona un profesional válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener la especialidad seleccionada del Spinner
            Especialidad especialidadSeleccionada = (Especialidad) specialtySpinner.getSelectedItem();

            // Verificar si la especialidad seleccionada es nula
            if (especialidadSeleccionada == null) {
                Toast.makeText(turnos.this, "Por favor, selecciona una especialidad válida.", Toast.LENGTH_SHORT).show();
                return;
            }

            int profesionalId = profesionalSeleccionado.getId();
            int especialidadId = especialidadSeleccionada.getId();

            // Validar que se haya seleccionado una fecha y una hora
            if (fechaSeleccionada == null || fechaSeleccionada.isEmpty()) {
                Toast.makeText(turnos.this, "Por favor, selecciona una fecha.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (horaSeleccionada == null || horaSeleccionada.isEmpty()) {
                Toast.makeText(turnos.this, "Por favor, selecciona una hora.", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = getUserId();
            System.out.println("userid: " + userId);

            // Crear el objeto JSON para el turno
            turnoData.put("id_user_id", userId);
            turnoData.put("paciente_id", pacienteId);
            turnoData.put("profesional_id", profesionalId);
            turnoData.put("hora_turno", horaSeleccionada);
            turnoData.put("fecha_turno", fechaSeleccionada);
            turnoData.put("especialidad_id", especialidadId);


        } catch (JSONException e) {
            Log.e("CrearTurno", "Error al crear el JSON: ", e);
            Toast.makeText(turnos.this, "Error al crear el turno.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Turno", "Creando turno: " + turnoData.toString());

        // Crear la solicitud JSON
        //String url = "https://reservasmedicas.ddns.net/api/v1/turnos/";
        String url = "https://reservasmedicas.ddns.net/nuevo_turno/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, turnoData,
                response -> {
                    Toast.makeText(turnos.this, "Turno creado exitosamente", Toast.LENGTH_SHORT).show();


                    // Llamo a clearForm para limpiar el formulario después de la creación exitosa del turno
                    clearForm();
                },
                error -> {
                    String errorMessage = "Error desconocido";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    Toast.makeText(turnos.this, "Error al crear el turno: " + errorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Configurar el tiempo de espera de la solicitud
        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }


    // Aca esta el método clearForm
    private void clearForm() {
        // Restablecer los Spinners a su posición inicial
        specialtySpinner.setSelection(0);
        professionalSpinner.setSelection(0);

        // Aca se vuelve los botones de selección de fecha y hora a su texto original
        openDatePickerButton.setText("Seleccionar Fecha");
        openTimePickerButton.setText("Seleccionar Hora");

        // Aca limpia las variables de fecha y hora seleccionadas
        fechaSeleccionada = "";
        horaSeleccionada = "";
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

        // Horarios para Leandro Martinez
        Map<String, List<String>> horariosLeandro = new HashMap<>();
        horariosLeandro.put("2024-10-26", Arrays.asList("09:00", "11:00", "12:30"));
        horariosLeandro.put("2024-10-27", Arrays.asList("10:00", "12:00", "14:00"));
        horariosLeandro.put("2024-10-28", Arrays.asList("11:00", "11:30", "13:00"));
        horariosLeandro.put("2024-10-29", Arrays.asList("11:30", "12:00", "13:30"));
        horariosPorEspecialistaYFecha.put("Leandro Martinez", horariosLeandro);

        // Horarios para Camila Medina
        Map<String, List<String>> horariosCamila = new HashMap<>();
        horariosCamila.put("2024-10-26", Arrays.asList("11:00", "11:30", "12:00", "12:30"));
        horariosCamila.put("2024-10-27", Arrays.asList("10:30", "13:00", "15:00"));
        horariosCamila.put("2024-10-28", Arrays.asList("11:30", "13:30", "17:00"));
        horariosPorEspecialistaYFecha.put("Camila Medina", horariosCamila);

        // Horarios para Juan Pedro García
        Map<String, List<String>> horariosJuan = new HashMap<>();
        horariosJuan.put("2024-10-25", Arrays.asList("14:00", "15:30", "16:00", "17:30"));
        horariosJuan.put("2024-10-26", Arrays.asList("14:30", "15:00", "16:30"));
        horariosJuan.put("2024-10-27", Arrays.asList("10:30", "13:00", "15:00"));
        horariosPorEspecialistaYFecha.put("Juan Pedro García", horariosJuan);

        // Horarios para Nicolás Pérez Ruiz
        Map<String, List<String>> horariosNicolas = new HashMap<>();
        horariosNicolas.put("2024-10-25", Arrays.asList("14:00", "15:30", "16:00", "17:30"));
        horariosNicolas.put("2024-10-26", Arrays.asList("14:30", "15:00", "16:30"));
        horariosPorEspecialistaYFecha.put("Nicolás Pérez Ruiz", horariosNicolas);

        // Horarios para Claudia Allende
        Map<String, List<String>> horariosClaudia = new HashMap<>();
        horariosClaudia.put("2024-10-26", Arrays.asList("09:00", "10:30", "12:00"));
        horariosClaudia.put("2024-10-27", Arrays.asList("09:30", "11:00", "13:00"));
        horariosClaudia.put("2024-10-27", Arrays.asList("10:30", "12:00", "13:30"));
        horariosPorEspecialistaYFecha.put("Claudia Allende", horariosClaudia);

        // Horarios para Martin Gomez
        Map<String, List<String>> horariosMartin = new HashMap<>();
        horariosMartin.put("2024-10-26", Arrays.asList("08:30", "10:00", "11:30"));
        horariosMartin.put("2024-10-27", Arrays.asList("09:00", "10:30", "12:00"));
        horariosMartin.put("2024-10-28", Arrays.asList("10:30", "13:30", "17:00"));
        horariosPorEspecialistaYFecha.put("Martin Gomez", horariosMartin);

        // Horarios para Raul Casas
        Map<String, List<String>> horariosRaul = new HashMap<>();
        horariosRaul.put("2024-10-26", Arrays.asList("08:00", "09:30", "11:00"));
        horariosRaul.put("2024-10-27", Arrays.asList("09:00", "10:30", "12:00"));
        horariosRaul.put("2024-10-28", Arrays.asList("10:30", "13:00", "15:00"));
        horariosPorEspecialistaYFecha.put("Raul Casas", horariosRaul);

        // Horarios para Rodrigo Cordoba
        Map<String, List<String>> horariosRodrigo = new HashMap<>();
        horariosRodrigo.put("2024-10-26", Arrays.asList("09:00", "10:30", "12:00"));
        horariosRodrigo.put("2024-10-27", Arrays.asList("10:00", "11:30", "13:00"));
        horariosRodrigo.put("2024-10-28", Arrays.asList("09:30", "12:00", "13:00"));
        horariosPorEspecialistaYFecha.put("Rodrigo Cordoba", horariosRodrigo);

        // Horarios para Mateo Lujan
        Map<String, List<String>> horariosMateo = new HashMap<>();
        horariosMateo.put("2024-10-25", Arrays.asList("14:00", "15:30", "16:00", "17:30"));
        horariosMateo.put("2024-10-26", Arrays.asList("14:30", "15:00", "16:30"));
        horariosMateo.put("2024-10-27", Arrays.asList("08:30", "12:00", "14:00"));
        horariosPorEspecialistaYFecha.put("Mateo Lujan", horariosMateo);

        Map<String, List<String>> horariosMarina = new HashMap<>();
        horariosMarina.put("2024-10-25", Arrays.asList("11:00", "11:30", "15:00", "16:30"));
        horariosMarina.put("2024-10-26", Arrays.asList("14:30", "15:00", "17:30"));
        horariosMarina.put("2024-10-27", Arrays.asList("08:30", "12:30", "14:00"));
        horariosPorEspecialistaYFecha.put("Marina Medrano", horariosMarina);

        Map<String, List<String>> horariosValentina = new HashMap<>();
        horariosValentina.put("2024-10-25", Arrays.asList("09:00", "10:30", "13:00", "13:30"));
        horariosValentina.put("2024-10-26", Arrays.asList("12:30", "15:00", "16:30"));
        horariosValentina.put("2024-10-27", Arrays.asList("08:30", "09:30", "12:00", "13:00"));
        horariosPorEspecialistaYFecha.put("Valentina Suarez", horariosValentina);

        Map<String, List<String>> horariosEmanuel = new HashMap<>();
        horariosEmanuel.put("2024-10-25", Arrays.asList("13:00", "13:30", "15:00", "16:30"));
        horariosEmanuel.put("2024-10-26", Arrays.asList("08:30", "11:00", "11:30"));
        horariosEmanuel.put("2024-10-27", Arrays.asList("09:30", "11:30", "12:00", "13:00"));
        horariosPorEspecialistaYFecha.put("Emanuel Romero", horariosEmanuel);
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