package com.example.reservasmedicasmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button btnEpecialidades,btnWeb, btnContacto, btnServicios, btnTurnos, btnRegistro, btnPerfil, btnLogin, btnLogout, btnAyuda;
    private ScreenOffReceiver screenOffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        // Inicializar botones
        btnContacto = findViewById(R.id.btn_contacto);
        btnServicios = findViewById(R.id.btn_servicios);
        btnTurnos = findViewById(R.id.btn_turnos);
        btnRegistro = findViewById(R.id.btn_registro);
        btnPerfil = findViewById(R.id.btn_dashboard);
        btnLogin = findViewById(R.id.btn_login);
        btnLogout = findViewById(R.id.btn_logout);
        btnAyuda = findViewById(R.id.btn_ayuda);
        btnEpecialidades = findViewById(R.id.btn_especialidad);
        btnWeb = findViewById(R.id.btn_web);

        // Inicializar el TextView para el mensaje de saludo
        TextView mensajeSaludo = findViewById(R.id.mensaje_saludo);

        // Comprobar si el usuario está logueado
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String first_name = sharedPreferences.getString("first_name", "Usuario");
        int id = sharedPreferences.getInt("id", -1);

        if (first_name != null && first_name.length() > 0) {
            first_name = first_name.substring(0, 1).toUpperCase() + first_name.substring(1).toLowerCase();
        }

        // Mostrar u ocultar botones según el estado de logueo
        if (isLoggedIn && id == 49) {
            String saludo = getString(R.string.saludo, first_name);
            mensajeSaludo.setText(saludo);
            btnLogout.setVisibility(View.VISIBLE);
            mensajeSaludo.setVisibility(View.VISIBLE);
            btnEpecialidades.setVisibility(View.VISIBLE);
            btnContacto.setVisibility(View.GONE);
            btnServicios.setVisibility(View.GONE);
            btnRegistro.setVisibility(View.GONE);
            btnPerfil.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            btnAyuda.setVisibility(View.GONE);
            btnTurnos.setVisibility(View.GONE);
        } else if (isLoggedIn && id != 49) {
            String saludo = getString(R.string.saludo, first_name);
            mensajeSaludo.setText(saludo);

            btnServicios.setVisibility(View.GONE);
            btnRegistro.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            btnAyuda.setVisibility(View.GONE);
        } else {
            btnTurnos.setVisibility(View.GONE);
            btnPerfil.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            mensajeSaludo.setVisibility(View.GONE);
        }

        // Asignar eventos a los botones
        btnContacto.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, contacto.class)));
        btnServicios.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, servicios.class)));
        btnTurnos.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, turnos.class)));
        btnRegistro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, registro.class)));
        btnPerfil.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, dashboard.class)));
        btnAyuda.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, VideoActivity.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, login.class)));
        btnEpecialidades.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, especialidades.class)));

        btnWeb.setOnClickListener(v -> {
            String url = "http://10.0.2.2:4200/inicio";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });


        btnLogout.setOnClickListener(v -> logoutAndRedirect());

        // Registrar el receptor para detectar cuando la pantalla se apaga
        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, filter);
    }

    private void logoutAndRedirect() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar si el usuario está logueado al volver a la app
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (!isLoggedIn) {
            // Si el usuario no está logueado, redirigir a la pantalla de inicio de sesión
            new MainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el receptor cuando se destruye la actividad
        unregisterReceiver(screenOffReceiver);
    }

    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                // Llama a la función de cierre de sesión automático al apagar la pantalla
                logoutAndRedirect();
            }
        }
    }
}


