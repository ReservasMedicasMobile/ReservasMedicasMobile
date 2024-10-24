package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Button btnEpecialidades, btnContacto, btnServicios, btnTurnos, btnRegistro, btnPerfil, btnLogin, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        // Inicializar botones
        btnContacto = findViewById(R.id.btn_contacto);
        btnServicios = findViewById(R.id.btn_servicios);
        btnTurnos = findViewById(R.id.btn_turnos);
        btnRegistro = findViewById(R.id.btn_registro);
        btnPerfil = findViewById(R.id.btn_dashboard);
        btnLogin = findViewById(R.id.btn_login);
        btnLogout = findViewById(R.id.btn_logout);

        btnEpecialidades = findViewById(R.id.btn_especialidad);

        // Inicializar el TextView para el mensaje de saludo
        TextView mensajeSaludo = findViewById(R.id.mensaje_saludo);

        // Comprobar si el usuario está logueado
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String first_name = sharedPreferences.getString("first_name", "Usuario");
        int id = sharedPreferences.getInt("id", -1);



        // Mostrar u ocultar botones según el estado de logueo
        {
            if(isLoggedIn && id == 49){
                String saludo = getString(R.string.saludo, first_name);
                mensajeSaludo.setText(saludo);
                btnLogout.setVisibility(View.VISIBLE);    // Solo para usuarios logueados
                mensajeSaludo.setVisibility(View.VISIBLE); // Ocultar mensaje si no está logueado
                btnEpecialidades.setVisibility(View.VISIBLE);
                btnContacto.setVisibility(View.GONE);
                btnServicios.setVisibility(View.GONE);
                btnRegistro.setVisibility(View.GONE);
                btnPerfil.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                btnTurnos.setVisibility(View.GONE);
            }else if (isLoggedIn && id !=49) {

                // Usar el string resource para el mensaje de saludo
                String saludo = getString(R.string.saludo, first_name);
                mensajeSaludo.setText(saludo); // Configurar el mensaje de saludo

                btnServicios.setVisibility(View.GONE); // Solo para usuarios no logueados
                btnRegistro.setVisibility(View.GONE);  // Solo para usuarios no logueados
                btnLogin.setVisibility(View.GONE);     // Solo para usuarios no logueados






            } else {
                btnTurnos.setVisibility(View.GONE);    // Solo para usuarios logueados
                btnPerfil.setVisibility(View.GONE);    // Solo para usuarios logueados
                btnLogout.setVisibility(View.GONE);    // Solo para usuarios logueados
                mensajeSaludo.setVisibility(View.GONE); // Ocultar mensaje si no está logueado

            }


        }

        // Asignar eventos a los botones
        btnContacto.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, contacto.class)));
        btnServicios.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, servicios.class)));
        btnTurnos.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, turnos.class)));
        btnRegistro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, registro.class)));
        btnPerfil.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, dashboard.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, login.class)));

        btnEpecialidades.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, especialidades.class)));

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();
            recreate(); // Refresca la actividad para aplicar los cambios
        });
    }

    
  
    /*@Override
    protected void onStop() {
        super.onStop();

        // Limpiar sesión cuando la app se detenga (cuando se cierra)
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Limpia todas las preferencias guardadas
        editor.apply();  // Aplica los cambios
    }*/
}
