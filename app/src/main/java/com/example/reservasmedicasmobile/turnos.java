package com.example.reservasmedicasmobile;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;


public class turnos extends AppCompatActivity {


    private Spinner specialtySpinner;
    private Spinner professionalSpinner;
    private Button openDatePickerButton;
    private Button openTimePickerButton;
    private Button timeSlotButton;
    private RequestQueue requestQueue; // Volley request queue
    private String fechaSeleccionada = ""; // Para almacenar la fecha seleccionada
    private String horaSeleccionada = "";  // Para almacenar la hora seleccionada


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


        // Cargar especialidades y profesionales desde el backend
        cargarEspecialidades();
        cargarProfesionales();


        openDatePickerButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                fechaSeleccionada = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                Toast.makeText(this, "Fecha seleccionada: " + fechaSeleccionada, Toast.LENGTH_SHORT).show();
            }, year, month, day);


            datePickerDialog.show();
        });


        openTimePickerButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfDay) -> {
                horaSeleccionada = String.format("%02d:%02d", hourOfDay, minuteOfDay);
                Toast.makeText(this, "Hora seleccionada: " + horaSeleccionada, Toast.LENGTH_SHORT).show();
            }, hour, minute, true);


            timePickerDialog.show();
        });


        timeSlotButton.setOnClickListener(v -> {
            // Lógica para crear turno en el backend
            crearTurno();
        });


        // Botón para volver atrás
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // Cierra la actividad actual y vuelve a la anterior
    }


    private void cargarEspecialidades() {
        String urlEspecialidades = "http://10.0.2.2:8000/api/v1/especialidad/";


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlEspecialidades,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> especialidadesList = new ArrayList<>();
                        especialidadesList.add("Seleccione una especialidad");


                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject especialidad = response.getJSONObject(i);
                                // Concatenar "especialidad" y "descripcion"
                                String especialidadCompleta = especialidad.getString("especialidad");
                                especialidadesList.add(especialidadCompleta); // Agregar la especialidad completa a la lista
                                Log.d("Turnos", "Especialidad cargada: " + especialidadCompleta); // Log para depuración
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(turnos.this, android.R.layout.simple_spinner_item, especialidadesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        specialtySpinner.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(turnos.this, "Error al cargar especialidades", Toast.LENGTH_SHORT).show();
                        Log.e("Turnos", "Error: " + error.getMessage()); // Log para errores
                    }
                });


        requestQueue.add(jsonArrayRequest);
    }


    private void cargarProfesionales() {
        String urlProfesionales = "http://10.0.2.2:8000/api/v1/profesionales/";


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlProfesionales,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> profesionalesList = new ArrayList<>();
                        profesionalesList.add("Seleccione un profesional");


                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject profesional = response.getJSONObject(i);
                                // Concatenar Nombre y Apellido
                                String nombreCompleto = profesional.getString("nombre") + " " + profesional.getString("apellido");
                                profesionalesList.add(nombreCompleto); // Agregar el nombre completo a la lista
                                Log.d("Turnos", "Profesional cargado: " + nombreCompleto); // Log para depuración
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(turnos.this, android.R.layout.simple_spinner_item, profesionalesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        professionalSpinner.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(turnos.this, "Error al cargar profesionales", Toast.LENGTH_SHORT).show();
                        Log.e("Turnos", "Error: " + error.getMessage()); // Log para errores
                    }
                });


        requestQueue.add(jsonArrayRequest);
    }


    private void crearTurno() {
        String urlCrearTurno = "http://10.0.2.2:8000/api/v1/turnos/";


        // Aquí colocas los datos del paciente que proporcionaste
        int pacienteId = 1; // ID del paciente (en este caso es 1, como en tu ejemplo)


        JSONObject turnoData = new JSONObject();
        try {
            String especialidadSeleccionada = specialtySpinner.getSelectedItem().toString();
            String profesionalSeleccionado = professionalSpinner.getSelectedItem().toString();


            // Asegúrate de validar la selección de todos los Spinners
            if ("Seleccione una especialidad".equals(especialidadSeleccionada) ||
                    "Seleccione un profesional".equals(profesionalSeleccionado)) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }


            // Coloca los datos en el JSON
            turnoData.put("especialidad", especialidadSeleccionada);
            turnoData.put("profesional", profesionalSeleccionado);
            turnoData.put("fecha", fechaSeleccionada); // Asegúrate de que esto tenga el formato correcto
            turnoData.put("hora", horaSeleccionada); // Asegúrate de que esto tenga el formato correcto
            turnoData.put("paciente_id", pacienteId); // Asegúrate de que este campo coincide con lo que espera el backend


        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Agregar log para depuración
        Log.d("Turnos", "Datos del turno: " + turnoData.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                urlCrearTurno,
                turnoData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(turnos.this, "Turno creado exitosamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error al crear turno: " + error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorBody = new String(error.networkResponse.data);
                            errorMessage += "\nDetalles: " + errorBody; // Agregar detalles del error
                        }
                        Toast.makeText(turnos.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });


        requestQueue.add(jsonObjectRequest);
    }


}
