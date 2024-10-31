package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class contacto extends AppCompatActivity {

    private Spinner spinnerOpcion;
    private EditText etFirstName, etLastName, etEmail, etPhoneNumber, etMessage;
    private Button btnSubmit;
    private String apiUrl = "https://reservasmedicas.ddns.net/api/v1/contacto/";

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
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spinnerOpcion = findViewById(R.id.spinner);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Listener para el botón de envío
        btnSubmit.setOnClickListener(v -> {
            System.out.println("hice click en el boton enviar");
            if (validateFields()) {
                sendFormWithJWT();
            }
        });

        // Configuración de las opciones del Spinner con la opción "Seleccionar" y "2"
        String[] opciones = {"Seleccionar", "Sugerencia", "Reclamo"};
        ArrayAdapter<String> opcionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        opcionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOpcion.setAdapter(opcionAdapter);
    }

    private boolean validateFields() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        // Validación para el campo Nombre (mínimo 2 caracteres, máximo 35)
        if (TextUtils.isEmpty(firstName) || !firstName.matches("[a-zA-ZÀ-ÿ'\\s]+")) {
            etFirstName.setError("El nombre es obligatorio y no debe contener números");
            return false;
        } else if (firstName.length() < 2) {
            etFirstName.setError("El nombre debe tener al menos 2 caracteres");
            return false;
        } else if (firstName.length() > 45) {
            etFirstName.setError("El nombre no debe exceder 45 caracteres");
            return false;
        }

        // Validación para el campo Apellido (mínimo 2 caracteres, máximo 35)
        if (TextUtils.isEmpty(lastName) || !lastName.matches("[a-zA-ZÀ-ÿ'\\s]+")) {
            etLastName.setError("El apellido es obligatorio y no debe contener números");
            return false;
        } else if (lastName.length() < 2) {
            etLastName.setError("El apellido debe tener al menos 2 caracteres");
            return false;
        } else if (lastName.length() > 45) {
            etLastName.setError("El apellido no debe exceder 45 caracteres");
            return false;
        }

        // Validación para el correo electrónico
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo electrónico inválido");
            return false;
        }

        // Validación para el número de teléfono (mínimo 10 dígitos)
        if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("[0-9]+") || phoneNumber.length() < 10) {
            etPhoneNumber.setError("Número de teléfono inválido (mínimo 10 dígitos)");
            return false;
        }

        // Validación para el campo Mensaje (mínimo 10 caracteres, máximo 200) y no permitir URLs
        if (TextUtils.isEmpty(message)) {
            etMessage.setError("El mensaje es obligatorio");
            return false;
        } else if (message.length() < 10) {
            etMessage.setError("El mensaje debe tener al menos 10 caracteres");
            return false;
        } else if (message.length() > 200) {
            etMessage.setError("El mensaje no debe exceder 200 caracteres");
            return false;
        } else if (Patterns.WEB_URL.matcher(message).find()) { // Nueva validación para URLs
            etMessage.setError("El mensaje no debe contener URLs");
            return false;
        }

        // Validación para el Spinner
        if (spinnerOpcion.getSelectedItem().toString().equals("Seleccionar")) {
            Toast.makeText(this, "Por favor, selecciona un tipo de consulta", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void sendFormWithJWT() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

        if (validateJWT(jwt)) {
            sendPostRequest(jwt);
        } else {
            Toast.makeText(contacto.this, "Error al enviar el formulario", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateJWT(String jwt) {
        return jwt != null && !jwt.isEmpty();
    }

    private void sendPostRequest(String jwt) {
        System.out.println("estoy en el paquete de envio");
        JSONObject postData = new JSONObject();

        try {
            postData.put("nombre", etFirstName.getText().toString().trim());
            postData.put("apellido", etLastName.getText().toString().trim());
            postData.put("correo", etEmail.getText().toString().trim());
            postData.put("telefono", etPhoneNumber.getText().toString().trim());
            // Obtener el valor correcto para el tipo de consulta
            String tipoDeConsulta = spinnerOpcion.getSelectedItem().toString();
            if (tipoDeConsulta.equals("Sugerencia")) {
                postData.put("tipo_de_consulta", 2); // Aquí se manda el código '2' en lugar del texto
            } else if (tipoDeConsulta.equals("Reclamo")) {
                postData.put("tipo_de_consulta", 3); // Código '3' para reclamo
            } else if (tipoDeConsulta.equals("Seleccionar")) {
                postData.put("tipo_de_consulta", 1); // Código por defecto si es necesario
            }

            postData.put("mensaje", etMessage.getText().toString().trim());
            postData.put("aviso", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String previaJson = postData.toString();
        System.out.println(previaJson);
        int timeoutMs = 10000;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiUrl,
                postData,
                response -> {
                    // Limpiar los campos y cambiar el texto del botón
                    clearFieldsAndChangeButtonText();
                    Toast.makeText(contacto.this, "Formulario enviado correctamente", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Toast.makeText(contacto.this, "Error al enviar el formulario: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // headers.put("Authorization", "Bearer " + jwt);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(jsonObjectRequest);
    }

    private void clearFieldsAndChangeButtonText() {
        // Limpiar los campos
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etPhoneNumber.setText("");
        etMessage.setText("");
        spinnerOpcion.setSelection(0); // Volver a la primera opción del Spinner ("Seleccionar")

        // Cambiar el texto del botón
        btnSubmit.setText("Pronto nos comunicaremos con usted");
        // Usar un Runnable para que el texto del botón vuelva a cambiar después de 5 segundos
        btnSubmit.postDelayed(() -> btnSubmit.setText("Enviar"), 5000);
    }
}







