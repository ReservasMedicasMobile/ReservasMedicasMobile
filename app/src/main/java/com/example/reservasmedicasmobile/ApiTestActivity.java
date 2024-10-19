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

        // Llamar al método para obtener los turnos de la API
        apiService.getTurnosFromApi(new ApiService.ApiTurnosResponseCallback() {
            @Override
            public void onSuccess() {
                // Actualizar el TextView con los turnos obtenidos
                apiTestText.setText("Turnos obtenidos con éxito. Revisa el Log para más detalles.");
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
