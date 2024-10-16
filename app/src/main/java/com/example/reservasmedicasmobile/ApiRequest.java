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

public class ApiRequest {
    private final RequestQueue requestQueue; // Campo final-modificado
    private static final String BASE_URL = "https://reservasmedicas.ddns.net/";

    public ApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

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

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
