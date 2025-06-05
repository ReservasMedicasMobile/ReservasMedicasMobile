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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.text.Editable;
import android.text.TextWatcher;

public class registro extends AppCompatActivity {

    private EditText usernameInput;
    private EditText first_nameInput;
    private EditText last_nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerBtn;
    private ImageButton backButton;
    private TextView passwordRequirements;
    private TextView passwordMismatchError;

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
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerBtn = findViewById(R.id.inicio_btn);
        passwordRequirements = findViewById(R.id.password_requirements);
        passwordMismatchError = findViewById(R.id.password_mismatch_error);

        // Validación dinámica de la contraseña
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                boolean hasUpperCase = !password.equals(password.toLowerCase());
                boolean hasLowerCase = !password.equals(password.toUpperCase());
                boolean hasDigit = password.matches(".*\\d.*");

                if (hasUpperCase && hasLowerCase && hasDigit) {
                    passwordRequirements.setVisibility(View.GONE);
                } else {
                    passwordRequirements.setVisibility(View.VISIBLE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        registerBtn.setOnClickListener(v -> {
            Log.d("Registro", "Presionó registro");
            registerUser();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(registro.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_login) {
                startActivity(new Intent(registro.this, login.class));
                return true;
            } else if (itemId == R.id.navigation_servicios) {
                startActivity(new Intent(registro.this, servicios.class));
                return true;
            } else {
                return false;
            }
        });

        // Limitar longitud de DNI
        usernameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});

        // Link para ir a login
        TextView login1 = findViewById(R.id.login1);
        login1.setOnClickListener(v -> startActivity(new Intent(registro.this, login.class)));
    }

    public void VolverInicio(View view) {
        startActivity(new Intent(registro.this, MainActivity.class));
    }

    private void registerUser() {
        Log.d("Registro", "Inicio de registro");
        String username = usernameInput.getText().toString().trim();
        String first_name = first_nameInput.getText().toString().trim();
        String last_name = last_nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("El DNI es requerido.");
            return;
        } else if (!username.matches("\\d+")) {
            usernameInput.setError("El DNI debe ser numérico");
            return;
        } else if (username.length() != 8) {
            usernameInput.setError("El DNI debe tener exactamente 8 dígitos");
            return;
        } else if (username.startsWith("0")) {
            usernameInput.setError("El DNI no puede empezar con '0'");
            return;
        } else if (username.matches("(\\d)\\1{7}")) {
            usernameInput.setError("El DNI no puede contener 8 números repetidos");
            return;
        }

        if (TextUtils.isEmpty(first_name)) {
            first_nameInput.setError("El nombre es requerido");
            return;
        } else if (!first_name.matches("[A-Za-z ]+")) {
            first_nameInput.setError("El nombre no puede contener caracteres especiales");
            return;
        } else if (first_name.length() < 3 || first_name.length() > 20) {
            first_nameInput.setError("El nombre es demasiado corto");
            return;
        }

        if (TextUtils.isEmpty(last_name)) {
            last_nameInput.setError("El apellido es requerido");
            return;
        } else if (!last_name.matches("[A-Za-z ]+")) {
            last_nameInput.setError("El apellido no puede contener caracteres especiales");
            return;
        } else if (last_name.length() < 2 || last_name.length() > 20) {
            last_nameInput.setError("El apellido es demasiado corto");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("El Correo electronico es requerido");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Correo electrónico inválido, debe seguir el formato de un correo válido ej: nombre22@gmail.com");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordRequirements.setText("La contraseña es requerida");
            passwordRequirements.setVisibility(View.VISIBLE);
            passwordMismatchError.setVisibility(View.GONE);
            return;
        } else if (!isPasswordValid(password)) {
            passwordRequirements.setText("La contraseña debe tener entre 8 y 16 caracteres, e incluir una mayúscula, números y símbolos");
            passwordRequirements.setVisibility(View.VISIBLE);
            passwordMismatchError.setVisibility(View.GONE);
            return;
        } else {
            passwordRequirements.setVisibility(View.GONE);
        }

        if (!password.equals(confirmPassword)) {
            passwordMismatchError.setVisibility(View.VISIBLE);
            return;
        } else {
            passwordMismatchError.setVisibility(View.GONE);
        }

        Log.d("Registro", "Validación superada");

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

        Log.d("JSON Request", jsonBody.toString());

        int timeoutMs = 10000;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        Log.d("RegistroResponse", response.toString());
                        String token = response.getString("token");

                        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", token);
                        editor.putString("first_name", first_name);
                        editor.putBoolean("is_logged_in", true);
                        editor.apply();

                        Toast.makeText(this, "Usuario registrado con éxito, ya puede iniciar sesión", Toast.LENGTH_SHORT).show();
                        clearForm();

                        startActivity(new Intent(registro.this, login.class));
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error procesando la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        if (statusCode == 400) {
                            Toast.makeText(this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                        } else if (statusCode == 404) {
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void clearForm() {
        usernameInput.setText("");
        first_nameInput.setText("");
        last_nameInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        confirmPasswordInput.setText("");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.length() <= 16
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")
                && password.matches(".*[A-Z].*");
    }
}
