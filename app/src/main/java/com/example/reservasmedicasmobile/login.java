package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
public class login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private ApiRequest apiRequest;
    CheckBox checkbox;
    GoogleApiClient googleApiClient;
    String SiteKey = "6LeUmWkqAAAAAGxptu-eDbZ2Xq7_ZwKSbqrBjok1";
    private int loginAttempts = 0; // Contador de intentos de inicio de sesión
    private static final int MAX_LOGIN_ATTEMPTS = 3; // Máximo de intentos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

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
        // Verifica si los intentos han superado el máximo permitido
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            Toast.makeText(this, "Tu cuenta está bloqueada temporalmente. Intenta más tarde.", Toast.LENGTH_SHORT).show();
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
                Log.d("Login", "Respuesta de la API: " + response.toString()); // Imprime la respuesta completa

                try {
                    loginAttempts = 0; // Resetear intentos al iniciar sesión correctamente

                    // Acceder al objeto 'user' y luego al 'first_name'
                    JSONObject user = response.getJSONObject("user");
                    String first_name = user.getString("first_name");
                    String token = response.getString("token");
                    int id = user.getInt("id");
                    System.out.println(id);

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
                Toast.makeText(login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                    Toast.makeText(login.this, "Demasiados intentos fallidos. Tu cuenta está bloqueada temporalmente.", Toast.LENGTH_SHORT).show();
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
}
