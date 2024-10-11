package com.example.reservasmedicasmobile;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.collections.UArraySortingKt;

public class contacto extends AppCompatActivity  {
    private Spinner spinnerOpcion;
    private EditText etFirstName, etLastName, etEmail, etPhoneNumber, etMessage;
    private Button btnSubmit;
    private String apiUrl = "http://TU_API_URL/api/v1/contacto/"; // Cambia esto por la URL de tu API

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        spinnerOpcion = findViewById(R.id.spinner);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            if (validateFields()) {
                sendFormWithJWT();
            }
        });

        String[] opcciones = {"TIPO DE CONSULTA" , "consulta","Sugerencia","Reclamo"};
        ArrayAdapter<String> opcionAdapter = new ArrayAdapter<>(this,R.layout.activity_contacto);
        spinnerOpcion.setAdapter(opcionAdapter);
    }

    private boolean validateFields() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || !firstName.matches("[a-zA-ZÀ-ÿ'\\s]+")) {
            etFirstName.setError("El nombre es obligatorio y no debe contener números");
            return false;
        }

        if (TextUtils.isEmpty(lastName) || !lastName.matches("[a-zA-ZÀ-ÿ'\\s]+")) {
            etLastName.setError("El apellido es obligatorio y no debe contener números");
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo electrónico inválido");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("[0-9]+") || phoneNumber.length() < 10) {
            etPhoneNumber.setError("Número de teléfono inválido");
            return false;
        }

        if (TextUtils.isEmpty(message)) {
            etMessage.setError("El mensaje es obligatorio");
            return false;
        }

        return true;
    }

    private void sendFormWithJWT() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"; // Token JWT simulado

        if (validateJWT(jwt)) {
            sendPostRequest(jwt);
        } else {
            Toast.makeText(contacto.this, "Error al enviar el formulario", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateJWT(String jwt) {
        return jwt != null && !jwt.isEmpty(); // Simular validación del token JWT
    }

    private void sendPostRequest(String jwt) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("nombre", etFirstName.getText().toString().trim());
            postData.put("apellido", etLastName.getText().toString().trim());
            postData.put("correo", etEmail.getText().toString().trim());
            postData.put("tipo_de_consulta", "Consulta"); // Puedes modificar esto según sea necesario
            postData.put("mensaje", etMessage.getText().toString().trim());
            postData.put("aviso", false); // Puedes modificar este campo según sea necesario
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(contacto.this, "Error al crear los datos del formulario", Toast.LENGTH_SHORT).show();
            return; // Salir si hay un error
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiUrl,
                postData,
                response -> Toast.makeText(contacto.this, "Formulario enviado correctamente", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(contacto.this, "Error al enviar el formulario", Toast.LENGTH_LONG).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwt);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
