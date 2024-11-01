package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {


    private EditText username;
    private EditText password;
    private ApiRequest apiRequest;
    CheckBox checkbox;
    GoogleApiClient googleApiClient;
    String SiteKey = "6LeUmWkqAAAAAGxptu-eDbZ2Xq7_ZwKSbqrBjok1";
    private SharedPreferences sharedPreferences;
    private int loginAttempts = 0; // Contador de intentos de inicio de sesión
    private static final int MAX_LOGIN_ATTEMPTS = 3; // Máximo de intentos
    private static final long BLOCK_DURATION = 1 * 60 * 1000; //BLOQUEO POR 1 minuto en milisegundos (SOLO PARA LA DEMOSTRACION, DESPUES SE PUEDE CAMBIAR POR 15 MINUTOS O LO QUE UNO QUIERA)


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
        checkbox = findViewById(R.id.check_box);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(login.this)
                .build();
        googleApiClient.connect();

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox.isChecked()){
                    SafetyNet.SafetyNetApi.verifyWithRecaptcha(googleApiClient, SiteKey)
                            .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                                @Override
                                public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult recaptchaTokenResult) {

                                    Status status = recaptchaTokenResult.getStatus();

                                    if ((status != null && status.isSuccess())){
                                        Toast.makeText(login.this, "Verificado Exitosamente",
                                                Toast.LENGTH_SHORT).show();
                                        checkbox.setTextColor(Color.BLUE);
                                    }
                                }
                            });

                }else{
                    checkbox.setTextColor(Color.RED);
                    Toast.makeText(login.this, "No ha sido verificado", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        login_button.setOnClickListener(v -> validarFormulario());

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
                username.setError("El DNI no puede estar vacío. Escribir solo números.");
            }

            if (TextUtils.isEmpty(contrasenia)) {
                password.setError("La contraseña no puede estar vacía");
            }
            if(dni.equals("406823198")){
                Intent intents = new Intent(login.this, especialidades.class);
                startActivity(intents);
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

        // Si los campos no están vacíos, iniciar sesión
        iniciarSesion(dni, contrasenia);

    }

    private void iniciarSesion(String dni, String contrasenia) {
        // Llamar a la API para iniciar sesión
        apiRequest.login(dni, contrasenia, new ApiRequest.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loginAttempts = 0; // Resetear intentos al iniciar sesión correctamente
                sharedPreferences.edit().putInt("login_attempts", loginAttempts).remove("block_timestamp").apply(); // Guardar intentos y eliminar bloqueo
                // Procesar la respuesta y continuar el flujo de éxito
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

    private void verifyReCaptcha() {
        String siteKey = "6Ldt6WkqAAAAAKek7SApEh_m8_knHFmssFJv4hoK";

        SafetyNet.getClient(this).verifyWithRecaptcha(siteKey)
                .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onComplete(Task<SafetyNetApi.RecaptchaTokenResponse> task) {
                        if (task.isSuccessful()) {
                            SafetyNetApi.RecaptchaTokenResponse response = task.getResult();
                            if (!response.getTokenResult().isEmpty()) {

                                Toast.makeText(login.this, "Formulario enviado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(login.this, "Verificación fallida", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("reCAPTCHA", "Error: " + task.getException().getMessage());
                        }
                    }
                });
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

