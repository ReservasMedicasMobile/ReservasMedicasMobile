package com.example.reservasmedicasmobile;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class especialidades extends AppCompatActivity {

    private FrameLayout ListaEspecialidadFragment;
    private TextView textViewEspecialidades;
    private RequestQueue rq;
    private Button imageButton, btnLogoutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_especialidades);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout1, new ListaEspecialidadFragment()).commit();

        rq = Volley.newRequestQueue(this);




        // Comprobar si el usuario está logueado
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if(!isLoggedIn){
            btnLogoutes.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", true);
                editor.apply();
                ;
            });
            startActivity();

        }


    }


    private void startActivity() {
        Intent intent = new Intent(especialidades.this, MainActivity.class);
        startActivity(intent);
    }

    public void listar(View v){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout1, new ListaEspecialidadFragment()).commit();

    }

    public void volver(View v){
        Intent intent = new Intent(especialidades.this, MainActivity.class);
        startActivity(intent);
    }

    public void agregar(View v){

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout1, new AgregarEspecialidadFragment()).commit();
    }

    public void Actualizar(View v){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout1, new ActualizarEspcialidadFragment()).commit();
    }

}