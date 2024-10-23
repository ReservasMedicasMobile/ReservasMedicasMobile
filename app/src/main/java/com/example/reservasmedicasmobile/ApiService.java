package com.example.reservasmedicasmobile;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.DefaultRetryPolicy;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ApiService {
    private final RequestQueue requestQueue;
    private static final String BASE_URL = "https://reservasmedicas.ddns.net/";

    public ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void fetchTurnos(String token, final ApiCallback callback) {
        String url = BASE_URL + "lista_turnos_usuario/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        callback.onSuccess(response); // Este callback espera un JSONArray
                    } catch (Exception e) {
                        Log.e("ApiService", "Error procesando la respuesta: ", e);
                        callback.onError(new VolleyError("Error procesando la respuesta."));
                    }
                },
                error -> {
                    String errorMessage;

                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 404:
                                errorMessage = "No se encontraron turnos.";
                                break;
                            case 500:
                                errorMessage = "Error del servidor. Intenta más tarde.";
                                break;
                            case 401:
                                errorMessage = "Credenciales de autenticación no proporcionadas.";
                                break;
                            default:
                                errorMessage = "Error desconocido.";
                                break;
                        }
                    } else {
                        errorMessage = "Error de conexión. Verifica tu red.";
                    }

                    Log.e("VolleyError", errorMessage);
                    callback.onError(new VolleyError(errorMessage));
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + token); // Usar el token con el prefijo 'Token'
                return headers;
            }
        };

        // Establece el tiempo de espera
        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonArrayRequest.setRetryPolicy(policy);

        // Agregar la solicitud a la cola
        requestQueue.add(jsonArrayRequest);
    }

    // Interfaz para el callback
    public interface ApiCallback {
        void onSuccess(JSONArray response); // Para la respuesta de los turnos
        void onError(VolleyError error); // Para manejar errores
    }
}
