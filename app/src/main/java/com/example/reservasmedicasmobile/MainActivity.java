package com.example.reservasmedicasmobile;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menudeopciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here.
        int id = item.getItemId();

        // analiza el evento del item presionado y redirecciona activity
        if (id == R.id.Inicio) {
            mostrarAlerta("Navegando a Inicio");
            // Navigate to Inicio
            return true;
        } else if (id == R.id.Contacto) {
            mostrarAlerta("Vas a Contacto");
            // va a Contacto
            return true;
        } else if (id == R.id.Especialidad) {
            mostrarAlerta("Vas a Especialidad");
            // va a Especialidad
            return true;
        } else if (id == R.id.Registro) {
            mostrarAlerta("Vas a Registro");
            // va a Registro
            return true;
        } else if (id == R.id.Dashboard) {
            mostrarAlerta("Vas a Dashboard");
            // va a Dashboard
            return true;
        } else if (id == R.id.Iniciar_Sesion) {
            mostrarAlerta("Vas a Iniciar Sesión");
            // va a Iniciar Sesion
            return true;
        } else if (id == R.id.Cerrar_Sesion) {
            mostrarAlerta("Vas a Cerrar Sesión");
            // va a Cerrar_Sesion
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void mostrarAlerta(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }


}
