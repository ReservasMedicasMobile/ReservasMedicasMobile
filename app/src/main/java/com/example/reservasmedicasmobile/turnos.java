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
    private ApiRequest apiRequest;
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
        apiRequest = new ApiRequest(this);


        specialtySpinner = findViewById(R.id.spinner_specialty);
        professionalSpinner = findViewById(R.id.spinner_professional);
        openDatePickerButton = findViewById(R.id.button_open_date_picker);
        openTimePickerButton = findViewById(R.id.button_open_time_picker);
        timeSlotButton = findViewById(R.id.button_time_slot);


        // Inicializar Volley
        requestQueue = Volley.newRequestQueue(this);


        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("auth_token", null);
        if (jwtToken != null) {
            cargarTurnos();
        } else {
            redirectToLogin();
        }

        inicializarHorariosPorEspecialistaYFecha();

        cargarEspecialidades();
        cargarProfesionales();
        cargarTurnos();

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

    private void redirectToLogin() {
        Toast.makeText(this, "No estás autenticado. Inicia sesión.", Toast.LENGTH_SHORT).show();
    }


    private void cargarTurnos() {
        // Obtener el token JWT desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("auth_token", null);

        // Verificar si el token JWT está disponible
        if (jwtToken != null) {
            apiRequest.getTurnos(jwtToken, new ApiRequest.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Procesar la respuesta y cargar los turnos
                    // Aquí deberías agregar el código para mostrar los turnos en tu interfaz
                    Toast.makeText(turnos.this, "Turnos cargados exitosamente", Toast.LENGTH_SHORT).show();
                    // Agregar lógica para mostrar los turnos en la UI
                }

                @Override
                public void onError(VolleyError error) {
                    String errorMessage;

                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 404:
                                errorMessage = "No se encontraron turnos para el usuario.";
                                break;
                            case 500:
                                errorMessage = "Error del servidor. Intenta más tarde.";
                                break;
                            default:
                                errorMessage = "Error desconocido.";
                                break;
                        }
                    } else {
                        errorMessage = "Error de conexión. Verifica tu red.";
                    }

                    Log.e("VolleyError", errorMessage); // Log para el error
                    Toast.makeText(turnos.this, "Error al cargar los turnos: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Si no hay usuario logueado
            Toast.makeText(this, "No hay usuario logueado.", Toast.LENGTH_SHORT).show();
        }
    }



    private void crearTurno() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("auth_token", null);
        JSONObject turnoData = new JSONObject();

        try {
            String especialidadSeleccionada = specialtySpinner.getSelectedItem().toString();
            String profesionalSeleccionado = professionalSpinner.getSelectedItem().toString();

            // Validación de campos
            if ("ESPECIALIDADES MÉDICAS".equals(especialidadSeleccionada) ||
                    "PROFESIONALES".equals(profesionalSeleccionado) ||
                    horaSeleccionada.isEmpty() ||
                    fechaSeleccionada.isEmpty()) {
                Toast.makeText(turnos.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el objeto JSON para el turno
            turnoData.put("especialidad", especialidadSeleccionada);
            turnoData.put("profesional", profesionalSeleccionado);
            turnoData.put("fecha", fechaSeleccionada);
            turnoData.put("hora", horaSeleccionada);

            apiRequest.crearTurno(jwtToken, turnoData, new ApiRequest.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Manejo de respuesta exitosa
                    Toast.makeText(turnos.this, "Turno creado exitosamente", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(VolleyError error) {
                    String errorMessage;

                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 400:
                                errorMessage = "Datos del turno son incorrectos.";
                                break;
                            case 404:
                                errorMessage = "El servicio no existe.";
                                break;
                            case 500:
                                errorMessage = "Error del servidor. Intenta más tarde.";
                                break;
                            default:
                                errorMessage = "Error desconocido.";
                                break;
                        }
                    } else {
                        errorMessage = "Error de conexión. Verifica tu red.";
                    }

                    Log.e("VolleyError", errorMessage); // Log para el error
                    Toast.makeText(turnos.this, "Error al crear el turno: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            // Manejo de excepciones
            e.printStackTrace();
            Toast.makeText(turnos.this, "Error al crear el turno: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
