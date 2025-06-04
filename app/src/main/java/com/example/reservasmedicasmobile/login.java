package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.hcaptcha.sdk.HCaptcha;
import com.hcaptcha.sdk.HCaptchaConfig;
import com.hcaptcha.sdk.HCaptchaException;


import org.json.JSONException;
import org.json.JSONObject;


public class login extends AppCompatActivity {

    private static final String SITE_KEY = "d1389988-f526-42c6-8786-7670d548dfa0";

    private EditText username;
    private EditText password;
    private ApiRequest apiRequest;
    private SharedPreferences sharedPreferences;
    private int loginAttempts = 0; // Contador de intentos de inicio de sesión
    private static final int MAX_LOGIN_ATTEMPTS = 3; // Máximo de intentos
    private static final long BLOCK_DURATION = 60 * 1000; //BLOQUEO POR 1 minuto en milisegundos (SOLO PARA LA DEMOSTRACION, DESPUES SE PUEDE CAMBIAR POR 15 MINUTOS O LO QUE UNO QUIERA)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        loginAttempts = sharedPreferences.getInt("login_attempts", 0); // Recuperar intentos fallidos

        // Verificar si la cuenta está bloqueada
        long blockTimestamp = sharedPreferences.getLong("block_timestamp", 0);
        if (blockTimestamp != 0 && System.currentTimeMillis() - blockTimestamp < BLOCK_DURATION) {
            Toast.makeText(this, "Tu cuenta está bloqueada temporalmente. Intenta más tarde.", Toast.LENGTH_SHORT).show();
        } else if (blockTimestamp != 0 && System.currentTimeMillis() - blockTimestamp >= BLOCK_DURATION) {
            // Bloqueo expirado, resetear
            sharedPreferences.edit().remove("block_timestamp").apply();
            loginAttempts = 0;
            sharedPreferences.edit().putInt("login_attempts", loginAttempts).apply();
        }

        //Mostrar contraseña
        ImageView imageViewTogglePassword = findViewById(R.id.imageViewTogglePassword);
        boolean[] isPasswordVisible = {false};

        imageViewTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Ocultar contraseña
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imageViewTogglePassword.setImageResource(R.drawable.ic_eye);  // Cambiar al ícono de "mostrar"
            } else {
                // Mostrar contraseña
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imageViewTogglePassword.setImageResource(R.drawable.ic_eye_off);  // Cambiar al ícono de "ocultar"
            }
            isPasswordVisible[0] = !isPasswordVisible[0];
            // Colocar el cursor al final del texto
            password.setSelection(password.getText().length());
        });

    //Barra navegacion inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(login.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_login) {
                Intent intent = new Intent(login.this, login.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_servicios) {
                Intent intent = new Intent(login.this, servicios.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

        // Referencia a los elementos de la interfaz
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button login_button = findViewById(R.id.login_button);

        // Instanciar ApiRequest
        apiRequest = new ApiRequest(this);

        // Listener botón iniciar sesión
        login_button.setOnClickListener(v -> {
            validarFormulario(); // Verificar campos antes de iniciar hCaptcha
        });

        // Link a registro
        TextView registro = findViewById(R.id.registro);
        registro.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registro.class);
            startActivity(intent);
        });
    }

    private void validarFormulario() {
        // Verificar si la cuenta está bloqueada
        long blockTimestamp = sharedPreferences.getLong("block_timestamp", 0);
        if (blockTimestamp != 0 && System.currentTimeMillis() - blockTimestamp < BLOCK_DURATION) {
            Toast.makeText(this, "Tu cuenta está bloqueada temporalmente. Intenta más tarde.", Toast.LENGTH_SHORT).show();
            return;
        } else if (blockTimestamp != 0 && System.currentTimeMillis() - blockTimestamp >= BLOCK_DURATION) {
            // Bloqueo expirado, resetear
            sharedPreferences.edit().remove("block_timestamp").apply();
            loginAttempts = 0;
            sharedPreferences.edit().putInt("login_attempts", loginAttempts).apply();
        }

        // Verifica si los intentos han superado el máximo permitido
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            sharedPreferences.edit().putLong("block_timestamp", System.currentTimeMillis()).apply();
            Toast.makeText(this, "Demasiados intentos fallidos. Tu cuenta está bloqueada por 1 minuto.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtén textos de los campos del formulario
        String dni = username.getText().toString().trim();
        String contrasenia = password.getText().toString().trim();

        // Verifica si los campos están vacíos
        if (TextUtils.isEmpty(dni) || TextUtils.isEmpty(contrasenia)) {
            Toast.makeText(login.this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();

            if (TextUtils.isEmpty(dni)) {
                username.setError("El Usuario es requerido.");
            }

            if (TextUtils.isEmpty(contrasenia)) {
                password.setError("La contraseña es requerida");
            }
            return;
        }

        // Verifica si el DNI tiene 8 dígitos
        if (!dni.matches("\\d{8}")) {
            username.setError("El DNI debe tener exactamente 8 dígitos.");
            return;
        }

        // Verifica si la contraseña tiene entre 8 y 16 caracteres
        if (contrasenia.length() < 8 || contrasenia.length() > 16) {
            password.setError("La contraseña debe tener entre 8 y 16 caracteres.");
            return;
        }

        // Si los campos están correctos, inicializa hCaptcha
        HCaptchaConfig hCaptchaConfig = HCaptchaConfig.builder()
                .siteKey(SITE_KEY)
                .locale("es")
                .build();

        HCaptcha.getClient(this).verifyWithHCaptcha(hCaptchaConfig)
                .addOnSuccessListener(hCaptchaTokenResponse -> {
                    // Verificación exitosa
                    String hCaptchaToken = hCaptchaTokenResponse.getTokenResult(); // Obtener el token del response
                    Log.d("hCaptcha", "Verificación exitosa. Token: " + hCaptchaToken);
                    iniciarSesion(dni, contrasenia, hCaptchaToken);
                })
                .addOnFailureListener(e -> {
                    // Error en la verificación
                    if (e instanceof HCaptchaException) {
                        Log.e("hCaptcha", "Verificación fallida. Error: " + e.getMessage());
                    }
                });
    }


    private void iniciarSesion(String dni, String contrasenia, String hCaptchaToken) {
        // Log para confirmar la llamada a iniciar sesión
        Log.d("hCaptcha", "Llamando a iniciar sesión con token hCaptcha");

        // Llamar a la API para iniciar sesión
        apiRequest.login(dni, contrasenia,hCaptchaToken, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loginAttempts = 0; // Resetear intentos al iniciar sesión correctamente
                sharedPreferences.edit().putInt("login_attempts", loginAttempts).remove("block_timestamp").apply(); // Guardar intentos y eliminar bloqueo
                // Procesar la respuesta y continuar el flujo exitoso
                try {
                    JSONObject user = response.getJSONObject("user");
                    String first_name = user.getString("first_name");
                    String token = response.getString("token");
                    int id = user.getInt("id");
                    // Guardar el nombre y el token
                    saveUserData(id, first_name, token);
                    Intent volverInicio = new Intent(login.this, MainActivity.class);
                    startActivity(volverInicio);
                    Toast.makeText(login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.e("Login", "Error al procesar la respuesta JSON", e);
                    Toast.makeText(login.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                loginAttempts++; // Incrementar contador al fallar
                sharedPreferences.edit().putInt("login_attempts", loginAttempts).apply(); // Guardar intentos
                Toast.makeText(login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                    sharedPreferences.edit().putLong("block_timestamp", System.currentTimeMillis()).apply();
                    Toast.makeText(login.this, "Demasiados intentos fallidos. Tu cuenta está bloqueada por 1 minuto.", Toast.LENGTH_SHORT).show();
                }
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

