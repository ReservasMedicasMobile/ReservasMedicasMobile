package com.example.reservasmedicasmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menuDeOpciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_de_opciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    // boton Contacto
    public void Contacto(View view){
        Intent contacto = new Intent(menuDeOpciones.this, contacto.class);
        startActivity(contacto);
    }

    // boton Especialidades
    public void Especialidades(View view){
        Intent especialidades = new Intent(menuDeOpciones.this, especialidades.class);
        startActivity(especialidades);
    }

    // boton Registro
    public void Registro(View view){
        Intent registro = new Intent(menuDeOpciones.this, registro.class);
        startActivity(registro);
    }

    // boton Dashboard
    public void Dashboard(View view){
        Intent dashboard = new Intent(menuDeOpciones.this, dashboard.class);
        startActivity(dashboard);
    }

    // boton Login
    public void Login(View view){
        Intent login = new Intent(menuDeOpciones.this, login.class);
        startActivity(login);
    }

}