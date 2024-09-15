package com.example.reservasmedicasmobile;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
            // Navigate to Inicio
            return true;
        } else if (id == R.id.Contacto) {
            // va a Contacto
            return true;
        } else if (id == R.id.Especialidad) {
            // va a Especialidad
            return true;
        } else if (id == R.id.Registro) {
            // va a Registro
            return true;
        } else if (id == R.id.Dashboard) {
            // va a Dashboard
            return true;
        } else if (id == R.id.Iniciar_Sesion) {
            // va a Iniciar Sesion
            return true;
        } else if (id == R.id.Cerrar_Sesion) {
            // va a Cerrar_Sesion
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
