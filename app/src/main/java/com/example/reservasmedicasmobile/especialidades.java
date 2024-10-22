package com.example.reservasmedicasmobile;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

    }

    public void listar(View v){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout1, new ListaEspecialidadFragment()).commit();

    }

    public void agregar(View v){

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout1, new AgregarEspecialidadFragment()).commit();
    }

    public void Actualizar(View v){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout1, new ActualizarEspcialidadFragment()).commit();
    }
}