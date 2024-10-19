package com.example.reservasmedicasmobile;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

public class ApiService {

    private static final String API_URL_PACIENTE = "https://reservasmedicas.ddns.net/api/v1/paciente/1/"; // Cambia por la URL de tu API
    private static final String API_URL_TURNOS = "https://reservasmedicas.ddns.net/api/v1/turnos/"; // Cambia por la URL de tu API para turnos
    private RequestQueue requestQueue;
    private Context context;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    // Método para obtener datos del paciente
    public void getDataFromApi(final ApiResponseCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL_PACIENTE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extraer los datos
                            int id = response.getInt("id");
                            String nombre = response.getString("nombre");
                            String apellido = response.getString("apellido");
                            String dni = response.getString("dni");

                            // Mostrar en Log
                            Log.d("API_Response", "ID: " + id + ", Nombre: " + nombre + " " + apellido + ", DNI: " + dni);

                            // Usar el callback para enviar los datos de vuelta
                            callback.onSuccess(nombre, apellido, dni);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Error al parsear la respuesta");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_Error", "Error al obtener los datos: " + error.getMessage());
                        callback.onError("Error al obtener los datos");
                    }
                }
        );

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    // Método para obtener los turnos
    public void getTurnosFromApi(final ApiTurnosResponseCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL_TURNOS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Mostrar en Log todos los turnos
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject turno = response.getJSONObject(i);
                                int idTurno = turno.getInt("id");
                                String fechaTurno = turno.getString("fecha_turno");
                                String horaTurno = turno.getString("hora_turno");
                                Log.d("API_Turnos", "Turno ID: " + idTurno + ", Fecha: " + fechaTurno + ", Hora: " + horaTurno);
                            }
                            callback.onSuccess(); // Llama al callback de éxito
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Error al parsear la respuesta");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_Error", "Error al obtener los turnos: " + error.getMessage());
                        callback.onError("Error al obtener los turnos");
                    }
                }
        );

        // Agregar la solicitud a la cola
        requestQueue.add(jsonArrayRequest);
    }

    // Interfaz de callback para paciente
    public interface ApiResponseCallback {
        void onSuccess(String nombre, String apellido, String dni);
        void onError(String errorMessage);
    }

    // Interfaz de callback para turnos
    public interface ApiTurnosResponseCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}
