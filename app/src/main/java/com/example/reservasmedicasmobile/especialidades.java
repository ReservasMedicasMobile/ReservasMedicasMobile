package com.example.reservasmedicasmobile;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.reservasmedicasmobile.adapter.DataModelAdapter;

import com.example.reservasmedicasmobile.modelo.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;



public class especialidades extends AppCompatActivity {


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

        textViewEspecialidades = findViewById(R.id.textViewEspecialidades);
        rq = Volley.newRequestQueue(this);

    }

    public void listar(View v){
        textViewEspecialidades.setText("");
        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/";
        JsonArrayRequest requerimiento = new JsonArrayRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0; i <response.length(); i++)
                        {
                            try {
                                JSONObject objeto = new JSONObject(response.get(i).toString());
                                textViewEspecialidades.append("Codigo" + objeto.getString("id") + "\n");
                                textViewEspecialidades.append("Especialidad" + objeto.getString("especialidad")+ "\n");
                                textViewEspecialidades.append("Descripcion" + objeto.getString("descripcion")+ "\n");
                                textViewEspecialidades.append("_____________________\n");

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(especialidades.this, "", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        rq.add(requerimiento);
    }



}