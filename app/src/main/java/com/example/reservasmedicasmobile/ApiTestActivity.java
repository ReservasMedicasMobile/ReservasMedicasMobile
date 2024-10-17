package com.example.reservasmedicasmobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ApiTestActivity extends AppCompatActivity {

    private TextView apiTestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);  // Este es el layout de tu activity

        apiTestText = findViewById(R.id.api_test_text);

        // Crear una instancia del servicio API
        ApiService apiService = new ApiService(this);

        // Llamar al método para obtener los datos de la API
        apiService.getDataFromApi(new ApiService.ApiResponseCallback() {
            @Override
            public void onSuccess(String nombre, String apellido, String dni) {
                // Actualizar el TextView con los datos recibidos
                apiTestText.setText("Nombre: " + nombre + " " + apellido + "\nDNI: " + dni);
            }

            @Override
            public void onError(String errorMessage) {
                // Manejar el error
                Log.e("API_Error", errorMessage);
                apiTestText.setText("Error: " + errorMessage);
            }
        });
    }
}
