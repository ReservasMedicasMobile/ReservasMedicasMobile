package com.example.reservasmedicasmobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login_button;

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
        login_button = findViewById(R.id.login_button);

        // Boton volver
        ImageButton backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {

            finish();
        });

        // Listener botón iniciar sesion
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarFormulario();
            }
        });
    }

    private void validarFormulario() {
        // Obtén textos de los campos del formulario
        String dni = username.getText().toString().trim();
        String contrasenia = password.getText().toString().trim();

        // Verifica si los campos están vacíos
        if (TextUtils.isEmpty(dni)) {
            username.setError("El DNI no puede estar vacío");
            return;
        }

        if (TextUtils.isEmpty(contrasenia)) {
            password.setError("La contraseña no puede estar vacía");
            return;
        }

        // Si los campos no están vacíos,  iniciar sesión
        iniciarSesion(dni, contrasenia);
    }

    private void iniciarSesion(String dni, String contrasenia) {
        // agregar la lógica para manejar el inicio de sesión con una base de datos
        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
    }
}