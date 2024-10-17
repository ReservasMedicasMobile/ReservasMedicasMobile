package com.example.reservasmedicasmobile;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.DefaultRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest {
    private final RequestQueue requestQueue; // Campo final-modificado
    private static final String BASE_URL = "https://reservasmedicas.ddns.net/";
    private static final String BASE_URL_TURNOS = "https://reservasmedicas.ddns.net/api/v1/turnos/";
    private static final String BASE_URL_NUEVO_TURNO = "https://reservasmedicas.ddns.net/nuevo_turno/";


    public ApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // Método para login
    public void login(String username, String password, final ApiCallback callback) {
        String url = BASE_URL + "login/";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            Log.e("ApiRequest", "Error creando JSON: ", e); // Uso de logging robusto-modificado
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                callback::onSuccess, // Lambda-methos reference para onResponse-modificado
                error -> {
                    String errorMessage;

                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 400:
                                errorMessage = "Datos ingresados son incorrectos.";
                                break;
                            case 404:
                                errorMessage = "El usuario no existe. Registrese.";
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

                    Log.e("VolleyError", errorMessage); // Muestra el error completo en Logcat
                    callback.onError(new VolleyError(errorMessage));
                });

        // Establece el tiempo de espera
        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    // Método para obtener turnos
    public void getTurnos(String jwtToken, final ApiCallback callback) {
        String url = BASE_URL_TURNOS;

        // Crear el encabezado de autorización
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                callback::onSuccess, // Lambda-method reference para onResponse
                error -> {
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

                    Log.e("VolleyError", errorMessage); // Muestra el error completo en Logcat
                    callback.onError(new VolleyError(errorMessage));
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers; // Agregar encabezados de autorización
            }
        };

        // Establece el tiempo de espera
        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    public void crearTurno(String jwtToken, JSONObject turnoData, final ApiCallback callback) {
        String url = BASE_URL_NUEVO_TURNO;

        // Crear el encabezado de autorización
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, turnoData,
                callback::onSuccess, // Lambda-method reference para onResponse
                error -> {
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

                    Log.e("VolleyError", errorMessage); // Muestra el error completo en Logcat
                    callback.onError(new VolleyError(errorMessage));
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers; // Agregar encabezados de autorización
            }
        };

        // Establece el tiempo de espera
        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    // Interfaz para los callbacks
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
