package com.reservasmedicasmobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class registro extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText confirmPasswordInput;
    private EditText passwordInput;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Inicializar vistas
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        passwordInput = findViewById(R.id.password_input);
        registerBtn = findViewById(R.id.register_btn);

        // Configurar el botón de registro
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar campos y registrar usuario
                registerUser();
            }
        });

        // Establecer filtros de entrada para el DNI
        usernameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

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

        // Validar correo electrónico
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("El correo electrónico es obligatorio");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Verifica formato de correo electrónico
            emailInput.setError("Correo electrónico inválido");
            return;
        }

        // Validar contraseñas
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("La contraseña es obligatoria");
            return;
        } else if (!isPasswordValid(password)) { // Verifica que la contraseña cumpla con los requisitos
            passwordInput.setError("La contraseña debe tener entre 8 y 16 caracteres, e incluir números y símbolos");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("La confirmación de contraseña es obligatoria");
            return;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Las contraseñas no coinciden");
            return;
        }

        // Aquí puedes añadir la lógica para registrar al usuario (como enviar los datos a un servidor)

        // Mensaje de éxito (se puede reemplazar esto con la lógica real de registro)
        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
    }

    private boolean isPasswordValid(String password) {
        // La contraseña debe tener entre 8 y 16 caracteres, e incluir al menos un número, un símbolo y una letra mayúscula
        return password.length() >= 8 && password.length() <= 16
                && password.matches(".*[0-9].*") // Al menos un número
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?].*") // Al menos un símbolo
                && password.matches(".*[A-Z].*"); // Al menos una letra mayúscula
    }

}
