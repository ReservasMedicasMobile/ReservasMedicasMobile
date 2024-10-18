package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar el TextView de bienvenida
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Obtener el nombre de usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null); // Valor por defecto
        String fullName = sharedPreferences.getString("full_name", "Usuario"); // Cambia 'first_name' y 'last_name' por 'full_name'

        // Comprobar si está logueado
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn && username != null) {
            // Configurar el texto de bienvenida y mostrarlo
            welcomeTextView.setText("Bienvenido " + fullName);
            welcomeTextView.setVisibility(View.VISIBLE);
        } else {
            // Ocultar el TextView si no está logueado
            welcomeTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Obtener el estado de inicio de sesión
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        // Inflar el menú de opciones según el estado de inicio de sesión
        if (isLoggedIn) {
            getMenuInflater().inflate(R.menu.menudeopciones_logueado, menu);
        } else {
            getMenuInflater().inflate(R.menu.menudeopciones_no_logueado, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Obtener el ID del item seleccionado
        int id = item.getItemId();

        // Manejar las opciones del menú de hamburguesa con if-else
        if (id == R.id.btn_contacto) {
            startActivity(new Intent(this, contacto.class));
            return true;
        } else if (id == R.id.btn_servicios) {
            startActivity(new Intent(this, servicios.class));
            return true;
        } else if (id == R.id.btn_registro) {
            startActivity(new Intent(this, registro.class));
            return true;
        } else if (id == R.id.btn_dashboard) {
            startActivity(new Intent(this, dashboard.class));
            return true;
        } else if (id == R.id.btn_login) {
            startActivity(new Intent(this, login.class));
            return true;
        } else if (id == R.id.btn_logout) {
            logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.putBoolean("is_logged_in", false);
        editor.remove("username"); // Eliminar el nombre de usuario
        editor.apply();

        // Redirigir a MainActivity
        startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(MainActivity.this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
        finish();
    }
}
