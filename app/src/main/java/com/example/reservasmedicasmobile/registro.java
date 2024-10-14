package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.Volley;
import android.util.Log;  // Importar la clase Log
import java.util.HashMap;
import java.util.Map;


public class registro extends AppCompatActivity {

    private EditText usernameInput;
    private EditText first_nameInput;
    private EditText last_nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    //private EditText confirmPasswordInput;
    private Button registerBtn;
    private ImageButton backButton; // Declaración de ImageButton

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar vistas
        usernameInput = findViewById(R.id.username_input);
        first_nameInput = findViewById(R.id.etFirstName);
        last_nameInput = findViewById(R.id.etLastName);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        //confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerBtn = findViewById(R.id.inicio_btn);
        backButton = findViewById(R.id.back_button); // Inicializar el botón

        // Configurar el botón de retroceso
        backButton.setOnClickListener(v -> {
            finish(); // Cierra la actividad y vuelve a la anterior
        });

        // Configurar el botón de registro
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("presione registro");
                // Validar campos y registrar usuario
                registerUser();
            }
        });

        // Establecer filtros de entrada para el DNI
        usernameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
    }

    public void VolverInicio(View view) {
        Intent volverInicio = new Intent(registro.this, MainActivity.class);
        startActivity(volverInicio);
    }

    private void registerUser() {
        System.out.println("entre a la funcion registro");
        String username = usernameInput.getText().toString().trim();
        String first_name = first_nameInput.getText().toString().trim();
        String last_name = last_nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        //String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validar DNI (username)
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("El DNI es obligatorio");
            return;
        } else if (!username.matches("\\d+")) { // Verifica que sea numérico
            usernameInput.setError("El DNI debe ser numérico");
            return;
        } else if (username.length() != 8) { // Verifica que tenga exactamente 8 dígitos
            usernameInput.setError("El DNI debe tener exactamente 8 dígitos");
            return;
        }
        // Validar nombre
        if (TextUtils.isEmpty(first_name)) {
            first_nameInput.setError("El nombre es obligatorio");
            return;
        } else if (!first_name.matches("[A-Za-z]+")) {
            first_nameInput.setError("El nombre solo puede contener letras");
            return;
        }

        // Validar apellido
        if (TextUtils.isEmpty(last_name)) {
            last_nameInput.setError("El apellido es obligatorio");
            return;
        } else if (!last_name.matches("[A-Za-z]+")) {
            last_nameInput.setError("El apellido solo puede contener letras");
            return;
        }

        // Validar correo electrónico
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("El correo electrónico es obligatorio");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Verifica formato de correo electrónico
            emailInput.setError("Correo electrónico inválido, debe seguir el formato de un correo valido ej:nombre22@gmail.com");
            return;
        }

        // Validar contraseñas
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("La contraseña es obligatoria");
            return;
        } else if (!isPasswordValid(password)) { // Verifica que la contraseña cumpla con los requisitos
            passwordInput.setError("La contraseña debe tener entre 8 y 16 caracteres, e incluir una mayuscula, números y símbolos");
            return;
        }
/*
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("La confirmación de contraseña es obligatoria");
            return;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Las contraseñas no coinciden");
            return;
        }*/

        System.out.println("pase la validacion ");

       String url = "https://reservasmedicas.ddns.net/register/";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("first_name", first_name);
            jsonBody.put("last_name", last_name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonBody.toString();
        Log.d("JSON Request", jsonString);  // Imprime el JSON en los logs

    int timeoutMs = 10000;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Código 200 o 201 - Registro exitoso
                    try {
                        // Imprimir el JSON de respuesta en la consola
                        Log.d("RegistroResponse", response.toString());

                        // Extraer el token del JSON de respuesta
                        String token = response.getString("token");

                        // Guardar el token en SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", token);
                        editor.putBoolean("is_logged_in", true); // alejo
                        editor.apply();

                        // Mostrar mensaje de éxito y limpiar el formulario
                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        clearForm();

                        // Puedes decidir qué hacer después del registro (ej. ir al inicio)
                        // Volver al inicio (MainActivity)
                        Intent intent = new Intent(registro.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        // Manejo del error de JSON
                        e.printStackTrace();
                        Toast.makeText(this, "Error procesando la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Manejar errores HTTP
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        if (statusCode == 400) {
                            // Usuario ya registrado
                            Toast.makeText(this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 404) {
                            // Sin conexión con el backend
                            Toast.makeText(this, "Error de conexión: no se pudo contactar al servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + statusCode, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };


        jsonObjectRequest.setRetryPolicy(new
    DefaultRetryPolicy(
            timeoutMs,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    ));

VolleySingleton.getInstance(this).
    addToRequestQueue(jsonObjectRequest);
}

    // Limpiar el formulario después de un registro exitoso
    private void clearForm() {
        usernameInput.setText("");
        first_nameInput.setText("");
        last_nameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        // confirmPasswordInput.setText(""); // si tienes un campo de confirmación de contraseña
    }


        private boolean isPasswordValid(String password) {
        // La contraseña debe tener entre 8 y 16 caracteres, e incluir al menos un número, un símbolo y una letra mayúscula
        return password.length() >= 8 && password.length() <= 16
                && password.matches(".*[0-9].*") // Al menos un número
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?].*") // Al menos un símbolo
                && password.matches(".*[A-Z].*"); // Al menos una letra mayúscula
    }
}