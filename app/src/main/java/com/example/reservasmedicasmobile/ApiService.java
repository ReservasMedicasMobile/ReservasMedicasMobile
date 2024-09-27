package com.example.reservasmedicasmobile;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiService {

    private static final String API_URL = "http://10.0.2.2:8000/api/v1/paciente/1/"; // Cambia por la URL de tu API
    private RequestQueue requestQueue;
    private Context context;

    public ApiService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getDataFromApi(final ApiResponseCallback callback) {
        // Crear la solicitud JSON para obtener los datos de la API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
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
                        // Manejar errores
                        Log.e("API_Error", "Error al obtener los datos: " + error.getMessage());
                        callback.onError("Error al obtener los datos");
                    }
                }
        );

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest);
    }

    // Interfaz de callback
    public interface ApiResponseCallback {
        void onSuccess(String nombre, String apellido, String dni);
        void onError(String errorMessage);
    }
}
