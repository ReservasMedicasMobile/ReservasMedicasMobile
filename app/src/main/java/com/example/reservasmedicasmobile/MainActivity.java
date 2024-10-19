package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button btnContacto, btnTurnos, btnRegistro, btnDashboard, btnLogin, btnLogout;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar los botones
        btnContacto = findViewById(R.id.btn_contacto);
        btnTurnos = findViewById(R.id.btn_turnos);
        btnRegistro = findViewById(R.id.btn_registro);
        btnDashboard = findViewById(R.id.btn_dashboard);
        btnLogin = findViewById(R.id.btn_login);
        btnLogout = findViewById(R.id.btn_logout);

        // Obtener el estado de inicio de sesión
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        // Actualizar visibilidad de botones según el estado de inicio de sesión
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        if (isLoggedIn) {
            // Mostrar botones para el usuario logueado
            btnContacto.setVisibility(View.VISIBLE);
            btnServicios.setVisibility(View.VISIBLE);
            btnRegistro.setVisibility(View.VISIBLE);
            btnDashboard.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnTurnos.setVisibility(View.GONE); // Oculta el botón de login
            btnLogout.setVisibility(View.VISIBLE); // Muestra el botón de logout
        } else {
            // Mostrar botones para el usuario no logueado
            btnContacto.setVisibility(View.VISIBLE);
            btnTurnos.setVisibility(View.VISIBLE);
            btnRegistro.setVisibility(View.VISIBLE);
            btnServicios.setVisibility(View.GONE);
            btnDashboard.setVisibility(View.GONE); // Oculta el botón de perfil
            btnLogin.setVisibility(View.VISIBLE); // Muestra el botón de login
            btnLogout.setVisibility(View.GONE); // Oculta el botón de logout
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        } else if (id == R.id.btn_turnos) {
            startActivity(new Intent(this, turnos.class));
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
        editor.apply();

        // Redirigir a MainActivity
        startActivity(new Intent(this, MainActivity.class));
        Toast.makeText(MainActivity.this, "Se cerró la sesión", Toast.LENGTH_SHORT).show();
        finish();
    }
}
