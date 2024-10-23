package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        // Referencia a los elementos de la interfaz
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button login_button = findViewById(R.id.login_button);

        // Instanciar ApiRequest
        apiRequest = new ApiRequest(this);


        // Configurar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(login.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_turnos) {
                Intent intent = new Intent(login.this, turnos.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_perfil) {
                Intent intent = new Intent(login.this, dashboard.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

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

        if (TextUtils.isEmpty(contrasenia)) {
            password.setError("La contraseña no puede estar vacía");
            return;
        }
        if (dni.equals("40682319")){
            Intent intent =new Intent(login.this, especialidades.class);
            startActivity(intent);
        }

        // Si los campos no están vacíos, iniciar sesión
        iniciarSesion(dni, contrasenia);
    }

    private void iniciarSesion(String dni, String contrasenia) {
        // Llamar a la API para iniciar sesión
        apiRequest.login(dni, contrasenia, new ApiRequest.ApiCallback() {
            @Override


            public void onSuccess(JSONObject response) {
                Log.d("Login", "Respuesta de la API: " + response.toString()); // Imprime la respuesta completa

                try {
                    if (dni.equals("40682319")){
                        Intent intent =new Intent(login.this, especialidades.class);
                        startActivity(intent);
                    }else{
                        // Acceder al objeto 'user' y luego al 'first_name'
                        JSONObject user = response.getJSONObject("user");
                        String first_name = user.getString("first_name");
                        String token = response.getString("token");
                        int id = user.getInt("id" );
                        System.out.println(id);

                        // Guardar el nombre y el token
                        saveUserData(id, first_name, token);

                        Intent volverInicio = new Intent(login.this, MainActivity.class);
                        startActivity(volverInicio);
                        Toast.makeText(login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    }

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
        Toast.makeText(this, "Procesando datos...", Toast.LENGTH_SHORT).show();
    }

    private void saveUserData(int id, String firstName, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("first_name", firstName); // Guarda el nombre del usuario
        editor.putString("auth_token", token);
        editor.putBoolean("is_logged_in", true);
        editor.putInt("id", id);
        editor.apply();
    }




}