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

import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private ApiRequest apiRequest;

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
        // Llamar a la API para iniciar sesión
        apiRequest.login(dni, contrasenia, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    // Guarda el token JWT
                    String token = response.getString("token");
                    Log.d("Login", "Token JWT: " + token); // verificar token guardado en logcat

                    saveToken(token);

                    // Redirigir a MainActivity
                    Intent volverInicio = new Intent(login.this, MainActivity.class);
                    startActivity(volverInicio);
                    Toast.makeText(login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    // Reemplaza printStackTrace con logging robusto -modificado
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
        Toast.makeText(this, "Procesando datos...", Toast.LENGTH_SHORT).show();
    }


    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.putBoolean("is_logged_in", true); // Guarda que el usuario está logueado
        editor.apply();

        Log.d("Login", "Token guardado en SharedPreferences: " + token); // verificar token guardado
    }
}
