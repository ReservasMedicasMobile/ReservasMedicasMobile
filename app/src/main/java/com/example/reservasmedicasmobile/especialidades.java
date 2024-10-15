package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.example.reservasmedicasmobile.interfaces.ApiService;
import com.example.reservasmedicasmobile.modelo.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class especialidades extends AppCompatActivity {


    private RecyclerView recyclerView;
    private DataModelAdapter dataModelAdapter;
    public List<DataModel> dataList;

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

        recyclerView = findViewById(R.id.recycler_view_especialidad);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();

        fetchData();


        
    }

    private void fetchData() {
        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                //String especialidad = jsonObject.getString("especialidad");
                                //String descripcion = jsonObject.getString("descripcion");
                                DataModel dataModel = new DataModel(id);
                                //DataModel dataModel = new DataModel(id, especialidad, descripcion);
                                dataList.add(dataModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dataModelAdapter = new DataModelAdapter(dataList);
                        recyclerView.setAdapter(dataModelAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }


}