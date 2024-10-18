package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private ApiRequest apiRequest;
    private static final String YOUR_API_SITE_KEY = "6LcBemQqAAAAAPtnMfeTXjgM14q3wSrWEqotfEgH";
    private static final String SECRET_KEY = "6LcBemQqAAAAAOsC2VJDWRVV10lzsiGUv3eUJKw2";

    private int intentosFallidos = 0; // Variable para contar los intentos fallidos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia a los elementos de la interfaz
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button login_button = findViewById(R.id.login_button);

        // Instanciar ApiRequest
        apiRequest = new ApiRequest(this);

        // Botón volver
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Listener botón iniciar sesión
        login_button.setOnClickListener(v -> validarFormulario());

        // Link a registro
        TextView registro = findViewById(R.id.registro);
        registro.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registro.class);
            startActivity(intent);
        });
    }

    private void validarFormulario() {
        // Obtén textos de los campos del formulario
        String dni = username.getText().toString().trim();
        String contrasenia = password.getText().toString().trim();

        // Verifica si los campos están vacíos
        if (TextUtils.isEmpty(dni) || TextUtils.isEmpty(contrasenia)) {
            Toast.makeText(login.this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();

            if (TextUtils.isEmpty(dni)) {
                username.setError("El DNI no puede estar vacío. Escribir solo números.");
            }

            if (TextUtils.isEmpty(contrasenia)) {
                password.setError("La contraseña no puede estar vacía");
                return;
            }
            return;
        }

        // Si los campos no están vacíos, iniciar sesión
        iniciarSesion(dni, contrasenia);
    }

    private void iniciarSesion(String dni, String contrasenia) {
        if (intentosFallidos >= 3) {
            // Si es el cuarto intento fallido o más, mostrar reCAPTCHA
            mostrarCaptcha(dni, contrasenia);
        } else {
            // Llamar a la API para iniciar sesión
            apiRequest.login(dni, contrasenia, new ApiRequest.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("Login", "Respuesta completa: " + response.toString());
                    try {
                        // Guarda el token JWT
                        String token = response.getString("token");
                        Log.d("Login", "Token JWT: " + token);

                        // Acceder al objeto user
                        JSONObject user = response.getJSONObject("user");
                        String firstName = user.getString("first_name");
                        String lastName = user.getString("last_name");

                        saveToken(token, firstName, lastName);

                        // Redirigir a MainActivity
                        Intent volverInicio = new Intent(login.this, MainActivity.class);
                        volverInicio.putExtra("first_name", firstName);
                        volverInicio.putExtra("last_name", lastName);
                        startActivity(volverInicio);
                        Toast.makeText(login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Log.e("Login", "Error al procesar la respuesta JSON", e);
                        Toast.makeText(login.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    // Incrementar intentos fallidos
                    intentosFallidos++;
                    String errorMessage = error.getMessage();
                    Toast.makeText(login.this, "Error de inicio de sesión: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void mostrarCaptcha(String dni, String contrasenia) {
        SafetyNet.getClient(this).verifyWithRecaptcha(YOUR_API_SITE_KEY)
                .addOnSuccessListener(this, response -> {
                    String userResponseToken = response.getTokenResult();
                    if (!userResponseToken.isEmpty()) {
                        // Validar el token en el servidor
                        validarTokenReCaptcha(userResponseToken, dni, contrasenia);
                    } else {
                        Toast.makeText(this, "Token de reCAPTCHA vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        Log.d("Login", "Error: " + statusCode);
                    } else {
                        Log.d("Login", "Error: " + e.getMessage());
                    }
                });

        Toast.makeText(this, "Procesando reCAPTCHA...", Toast.LENGTH_SHORT).show();
    }

    private void validarTokenReCaptcha(String userResponseToken, String dni, String contrasenia) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("secret", SECRET_KEY);
            jsonBody.put("response", userResponseToken);
        } catch (JSONException e) {
            Log.e("Login", "Error creando JSON para la verificación de reCAPTCHA", e);
        }

        String url = "https://www.google.com/recaptcha/api/siteverify";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            // Llamar a la API para iniciar sesión
                            apiRequest.login(dni, contrasenia, new ApiRequest.ApiCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.d("Login", "Respuesta completa: " + response.toString());
                                    try {
                                        // Guarda el token JWT
                                        String token = response.getString("token");
                                        Log.d("Login", "Token JWT: " + token);

                                        // Acceder al objeto user
                                        JSONObject user = response.getJSONObject("user");
                                        String firstName = user.getString("first_name");
                                        String lastName = user.getString("last_name");

                                        saveToken(token, firstName, lastName);

                                        // Redirigir a MainActivity
                                        Intent volverInicio = new Intent(login.this, MainActivity.class);
                                        volverInicio.putExtra("first_name", firstName);
                                        volverInicio.putExtra("last_name", lastName);
                                        startActivity(volverInicio);
                                        Toast.makeText(login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        Log.e("Login", "Error al procesar la respuesta JSON", e);
                                        Toast.makeText(login.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    String errorMessage = error.getMessage();
                                    Toast.makeText(login.this, "Error de inicio de sesión: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Error de verificación de reCAPTCHA", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("Login", "Error al procesar la respuesta de reCAPTCHA", e);
                    }
                },
                error -> Log.e("Login", "Error en la verificación de reCAPTCHA: " + error.getMessage())
        );

        // Agregar la solicitud a la cola
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void saveToken(String token, String firstName, String lastName) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.putBoolean("is_logged_in", true); // Guarda que el usuario está logueado
        editor.putString("username", username.getText().toString().trim());
        editor.putString("full_name", firstName + " " + lastName);
        editor.apply();

        Log.d("Login", "Nombre de usuario guardado: " + username.getText().toString().trim());
        Log.d("Login", "Nombre guardado: " + firstName + " " + lastName);
        Log.d("Login", "Token guardado en SharedPreferences: " + token);
    }
}
