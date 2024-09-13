package com.reservasmedicasmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhoneNumber, etMessage;
    private Button btnSubmit, btnBackToHome;
    private TextView tvData;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inicializar vistas
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBackToHome = findViewById(R.id.btnBackToHome); // Nuevo botón
        tvData = findViewById(R.id.tvData);

        databaseHelper = new DatabaseHelper(this);

        // Evento del botón Enviar
        btnSubmit.setOnClickListener(view -> {
            if (validateForm()) {
                long result = databaseHelper.addContact(
                        etFirstName.getText().toString(),
                        etLastName.getText().toString(),
                        etEmail.getText().toString(),
                        etPhoneNumber.getText().toString(),
                        etMessage.getText().toString()
                );

                if (result != -1) {
                    Toast.makeText(ContactActivity.this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                    // Muestra el primer mensaje
                    tvData.setText("Sus datos han sido guardados exitosamente");
                    tvData.setVisibility(View.VISIBLE);

                    // Cambia el texto y muestra el segundo mensaje después de un breve retraso
                    tvData.postDelayed(() -> {
                        tvData.setText("Su mensaje ha sido enviado, pronto nos comunicaremos con Usted!");
                    }, 2000); // Retraso de 2 segundos

                    clearForm();
                } else {
                    Toast.makeText(ContactActivity.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Evento del botón Volver al Inicio
        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(ContactActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });
    }

    // Método para validar el formulario
    private boolean validateForm() {
        if (etFirstName.getText().toString().isEmpty() || !etFirstName.getText().toString().matches("[a-zA-ZÀ-ÿ'\\s]+")) {
            etFirstName.setError("El nombre es obligatorio y no debe contener números");
            return false;
        }
        if (etLastName.getText().toString().isEmpty() || !etLastName.getText().toString().matches("[a-zA-ZÀ-ÿ'\\s]+")) {
            etLastName.setError("El apellido es obligatorio y no debe contener números");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError("Email no válido");
            return false;
        }
        if (etPhoneNumber.getText().toString().isEmpty() || !etPhoneNumber.getText().toString().matches("[0-9]+")) {
            etPhoneNumber.setError("El teléfono es obligatorio y debe contener solo números");
            return false;
        }
        if (etMessage.getText().toString().isEmpty()) {
            etMessage.setError("El mensaje es obligatorio");
            return false;
        }
        return true;
    }

    // Método para limpiar el formulario
    private void clearForm() {
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etPhoneNumber.setText("");
        etMessage.setText("");
    }
}

