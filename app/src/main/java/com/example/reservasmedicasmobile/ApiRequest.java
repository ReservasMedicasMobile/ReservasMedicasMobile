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
    private final RequestQueue requestQueue;
    private static final String BASE_URL = "https://reservasmedicas.ddns.net/";

    public ApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void login(String username, String password, String hCaptchaToken, final ApiCallback callback) {
        String url = BASE_URL + "login/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("hCaptchaToken", hCaptchaToken); // Agregar el token de hCaptcha
        } catch (JSONException e) {
            Log.e("ApiRequest", "Error creando JSON: ", e);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                callback::onSuccess,
                error -> {
                    String errorMessage = "Credenciales incorrectas"; // Mensaje genérico
                    if (error.networkResponse != null) {
                        Log.e("VolleyError", "Código de respuesta: " + error.networkResponse.statusCode);
                        switch (error.networkResponse.statusCode) {
                            case 400:
                                errorMessage = "Datos ingresados son incorrectos.";
                                break;
                            case 404:
                                errorMessage = "El usuario no existe. Regístrese.";
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
                    Log.e("VolleyError", errorMessage);
                    callback.onError(new VolleyError(errorMessage));
                });
        int socketTimeout = 30000; // 30 segundos
        DefaultRetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);
    }


    // Interfaz para los callbacks
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
