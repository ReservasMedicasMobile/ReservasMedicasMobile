package com.example.reservasmedicasmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import java.text.SimpleDateFormat;
import java.util.*;

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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


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

        // Cargar los turnos reservados desde SharedPreferences al iniciar la actividad
        cargarTurnosReservados();

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
            public void onNothingSelected(AdapterView<?> parentView) {
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
                if (especialidadId != 0) {
                    cargarProfesionales(especialidadId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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
                            // Crea un objeto Paciente y lo añade
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

    public void limpiarCampos() {
        // Limpiar el Spinner de Especialidades
        specialtySpinner.setSelection(0); // O el valor predeterminado que desees

        // Limpiar el Spinner de Profesionales
        professionalSpinner.setSelection(0); // O el valor predeterminado que desees

        // Limpiar los botones de fecha y hora
        openDatePickerButton.setText("Fecha: ");
        fechaSeleccionada = ""; // Reiniciar la fecha seleccionada

        openTimePickerButton.setText("Horario: ");
        horaSeleccionada = ""; // Reiniciar la hora seleccionada
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

            limpiarCampos();


        } catch (JSONException e) {
            Log.e("CrearTurno", "Error al crear el JSON: ", e);
            Toast.makeText(turnos.this, "Error al crear el turno.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Turno", "Creando turno: " + turnoData.toString());

        //String url = "https://reservasmedicas.ddns.net/api/v1/turnos/";
        String url = "https://reservasmedicas.ddns.net/nuevo_turno/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, turnoData,
                response -> {
                    Toast.makeText(turnos.this, "Turno creado exitosamente", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = "Error desconocido";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    Toast.makeText(turnos.this, "Error al crear el turno: " + errorMessage, Toast.LENGTH_SHORT).show();
                }) {

            @Override public Map<String, String> getHeaders() {
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
        List<Integer> diasDeAtencion;

        public Profesional(int id,  String nombreCompleto, int especialidadId) {
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

                            if (especialidad == especialidadId) {
                                profesionalesList.add(new Profesional(id, nombreCompleto, especialidad));
                            }
                            Log.d("Turnos", "Profesional cargado: " + nombreCompleto + " (ID: " + id + ")");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Cargar el Spinner
                    ArrayAdapter<Profesional> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_turnos, profesionalesList);
                    adapter.setDropDownViewResource(R.layout.spinner_item_turnos);
                    professionalSpinner.setAdapter(adapter);

                    // --- Aquí inicializás los horarios para estos profesionales ---
                    // Quitá la opción "PROFESIONALES" si no querés contarla
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        profesionalesList.removeIf(p -> p.getId() == 0);
                    }
                    inicializarHorariosPorEspecialistaYFecha(profesionalesList);
                },
                error -> {
                    Toast.makeText(this, "Error al cargar profesionales", Toast.LENGTH_SHORT).show();
                    Log.e("Turnos", "Error: " + error.getMessage());
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    // 1) Adaptar la función para recibir la lista de profesionales
    private void inicializarHorariosPorEspecialistaYFecha(List<Profesional> profesionales) {
        horariosPorEspecialistaYFecha = new HashMap<>();

        // Días y horarios "tipo Leandro"
        List<Integer> diasLeandro = Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY);
        List<String> horariosLeandro = Arrays.asList("09:00", "10:00", "11:00", "13:00", "15:00");

        // Días y horarios "tipo Camila"
        List<Integer> diasCamila = Arrays.asList(Calendar.TUESDAY, Calendar.THURSDAY);
        List<String> horariosCamila = Arrays.asList("10:00", "11:30", "13:30", "14:30", "16:00");

        int mitad = profesionales.size() / 2;

        for (int i = 0; i < profesionales.size(); i++) {
            Profesional prof = profesionales.get(i);
            String nombreCompleto = prof.getNombreCompleto();

            List<Integer> diasAsignados;
            List<String> horariosAsignados;

            // Dividir profesionales en dos grupos y asignar días y horarios
            if (i < mitad) {
                diasAsignados = diasLeandro;
                horariosAsignados = horariosLeandro;
            } else {
                diasAsignados = diasCamila;
                horariosAsignados = horariosCamila;
            }

            // Generar fechas para los días asignados (por ej. hasta fin de año)
            List<String> fechas = generarFechasParaDiasFijosProfesional(diasAsignados);

            // Asociar horarios a cada fecha
            Map<String, List<String>> horariosPorFecha = new HashMap<>();
            for (String fecha : fechas) {
                horariosPorFecha.put(fecha, new ArrayList<>(horariosAsignados));
            }

            // Guardar la info en el mapa general
            horariosPorEspecialistaYFecha.put(nombreCompleto, horariosPorFecha);
        }
    }

    // 2) Función para generar las fechas (ya la tenés)
    private List<String> generarFechasParaDiasFijosProfesional(List<Integer> diasAtencion) {
        List<String> fechas = new ArrayList<>();
        Calendar calendario = Calendar.getInstance(); // hoy
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Fecha límite: 31 de diciembre del año actual
        Calendar finDeAno = Calendar.getInstance();
        finDeAno.set(Calendar.MONTH, Calendar.DECEMBER);
        finDeAno.set(Calendar.DAY_OF_MONTH, 31);

        while (!calendario.after(finDeAno)) {
            int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);
            if (diasAtencion.contains(diaSemana)) {
                fechas.add(formato.format(calendario.getTime()));
            }
            calendario.add(Calendar.DAY_OF_MONTH, 1);
        }

        return fechas;
    }

    private void mostrarFechasDisponibles() {
        String especialistaSeleccionado = professionalSpinner.getSelectedItem().toString();
        if (!horariosPorEspecialistaYFecha.containsKey(especialistaSeleccionado)) {
            Toast.makeText(this, "Selecciona un profesional válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<String>> fechasHorarios = horariosPorEspecialistaYFecha.get(especialistaSeleccionado);
        List<String> fechasDisponibles = new ArrayList<>(fechasHorarios.keySet());

        // Usamos el formato yyyy-MM-dd para parsear y ordenar
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

        // Preparar las fechas formateadas para mostrar
        List<String> fechasFormateadas = new ArrayList<>();
        for (Date fechaDate : fechasConvertidas) {
            String fechaOriginal = formatoFecha.format(fechaDate); // "yyyy-MM-dd"
            String fechaConDia = formatearFecha(fechaOriginal); // "viernes yyyy-MM-dd"
            fechasFormateadas.add(fechaConDia);
        }

        // Mostrar las fechas disponibles en un diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, fechasFormateadas);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener la fecha original sin el día de la semana
            String fechaFormateada = fechasFormateadas.get(position); // "viernes yyyy-MM-dd"
            String fechaOriginal = fechaFormateada.substring(fechaFormateada.indexOf(' ') + 1); // "yyyy-MM-dd"
            fechaSeleccionada = fechaOriginal;
            openDatePickerButton.setText("Fecha: " + fechaFormateada);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Seleccionar", (dialog, which) -> {
            if (!fechaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Fecha seleccionada: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

   /* private void mostrarHorariosDisponibles() {
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

        /*.Builder builder = new AlertDialog.Builder(this);

        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, horariosDisponibles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(listView);

        // Filtrar los horarios reservados y crear una lista de horarios disponibles
        List<String> horariosDisponiblesActualizados = new ArrayList<>();
        for (String hora : horariosDisponibles) {
            // Verificar si el turno ya está reservado
            if (!turnosReservados.contains(fechaSeleccionada + " " + hora)) {
                horariosDisponiblesActualizados.add(hora);
            }
        }

        // Mostrar los horarios disponibles (sin los reservados)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, horariosDisponiblesActualizados);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            horaSeleccionada = horariosDisponibles.get(position);
            openTimePickerButton.setText("Horario: " + horaSeleccionada);
        });

        /*builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Seleccionar", (dialog, which) -> {
            if (!horaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Hora seleccionada: " + horaSeleccionada, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Seleccionar", (dialog, which) -> {
            if (!horaSeleccionada.isEmpty()) {
                // Agregar el turno seleccionado al conjunto de turnos reservados
                turnosReservados.add(fechaSeleccionada + " " + horaSeleccionada);
                Toast.makeText(this, "Hora seleccionada: " + horaSeleccionada, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/




    //Almaceno los turnos reservados (SharedPreferences)
    Set<String> turnosReservados = new HashSet<>();

    private void cargarTurnosReservados() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Recuperamos los turnos reservados guardados como un conjunto de cadenas (Set<String>)
        Set<String> turnos = sharedPreferences.getStringSet("turnos_reservados", new HashSet<>());
        turnosReservados.addAll(turnos);
    }

    private void guardarTurnosReservados() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Guardamos el conjunto de turnos reservados en SharedPreferences
        editor.putStringSet("turnos_reservados", turnosReservados);
        editor.apply(); // Guardamos los cambios de manera asíncrona
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

        // Filtrar los horarios reservados y crear una lista de horarios disponibles
        List<String> horariosDisponiblesActualizados = new ArrayList<>();
        for (String hora : horariosDisponibles) {
            // Verificar si el turno ya está reservado
            if (!turnosReservados.contains(fechaSeleccionada + " " + hora)) {
                horariosDisponiblesActualizados.add(hora);
            }
        }

        // Mostrar los horarios disponibles (sin los reservados)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, horariosDisponiblesActualizados);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            horaSeleccionada = horariosDisponiblesActualizados.get(position);
            openTimePickerButton.setText("Horario: " + horaSeleccionada);
        });

        builder.setNegativeButton("Cancelar", null);

        // Guardar el turno reservado al confirmar la selección
        builder.setPositiveButton("Seleccionar", (dialog, which) -> {
            if (!horaSeleccionada.isEmpty()) {
                // Agregar el turno seleccionado al conjunto de turnos reservados
                turnosReservados.add(fechaSeleccionada + " " + horaSeleccionada);
                // Guardar los turnos reservados en SharedPreferences
                guardarTurnosReservados();
                Toast.makeText(this, "Hora seleccionada: " + horaSeleccionada, Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public static String formatearFecha(String fecha) {
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("EEEE yyyy-MM-dd", new Locale("es", "ES"));
        try {
            Date fechaDate = formatoEntrada.parse(fecha);
            String fechaFormateada = formatoSalida.format(fechaDate);

            return fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);
        } catch (ParseException e) {
            e.printStackTrace();
            return fecha;
        }
    }
}