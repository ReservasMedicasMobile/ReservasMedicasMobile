package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class contacto extends AppCompatActivity {

    private TextView tvData;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        // Inicializar los elementos de la UI
        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        EditText etMessage = findViewById(R.id.etMessage);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnBack = findViewById(R.id.btnBack);

        // Configuración del botón back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }
}
