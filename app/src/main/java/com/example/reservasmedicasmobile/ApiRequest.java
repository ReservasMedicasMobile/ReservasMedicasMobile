package com.example.reservasmedicasmobile;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiRequest {
    private RequestQueue requestQueue;
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
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.toString(); // Captura el error completo
                        Log.e("VolleyError", errorMessage); // Muestra el error completo en Logcat
                        callback.onError(new VolleyError(errorMessage));
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
