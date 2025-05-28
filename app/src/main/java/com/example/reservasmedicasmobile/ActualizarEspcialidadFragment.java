package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reservasmedicasmobile.adapter.MyAdapter;
import com.example.reservasmedicasmobile.modelo.DataModel2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActualizarEspcialidadFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<DataModel2> dataList;
    private Button deleteButton;






    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actualizar_espcialidad, container, false);
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList, this);
        recyclerView.setAdapter(adapter);
        deleteButton = view.findViewById(R.id.deleteButton);

        fetchData();




        return view;
    }

    private void fetchData() {
        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            DataModel2 data= new DataModel2(jsonObject.getInt("id"), jsonObject.getString("especialidad"), jsonObject.getString("descripcion"));
                            dataList.add(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {

                    Log.e("Volley", "Error: " + error.getMessage());
                });

        queue.add(jsonArrayRequest);
    }

    private boolean esTextoValido(String texto) {
        // Comprobar si el texto tiene entre 4 y 45 caracteres
        if (texto.length() < 4 || texto.length() > 100) {
            Toast.makeText(getActivity(), "El texto debe tener entre 4 y 100 caracteres.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Comprobar si el texto contiene caracteres especiales
        if (contieneCaracteresEspeciales(texto)) {
            Toast.makeText(getActivity(), "El texto no debe contener caracteres especiales.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Comprobar si el texto contiene URLs
        if (contieneURL(texto)) {
            Toast.makeText(getActivity(), "El texto no debe contener URLs.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Comprobar si el texto contiene caracteres de SQL Injection (comillas simples o dobles, etc.)
        if (contieneSQLInjection(texto)) {
            Toast.makeText(getActivity(), "El texto contiene caracteres potencialmente peligrosos (SQL Injection).", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean contieneCaracteresEspeciales(String texto) {
        // Expresión regular para detectar caracteres especiales
        String regex = "[^a-zA-Z0-9\\s]";
        return texto.matches(".*" + regex + ".*");
    }

    private boolean contieneURL(String texto) {
        // Usar la clase Patterns para detectar URLs
        return Patterns.WEB_URL.matcher(texto).find();
    }

    private boolean contieneSQLInjection(String texto) {
        // Detectar caracteres comunes en SQL Injection (puedes expandir esta lista)
        String regexSQL = "(;|--|\\|\\||\\/\\*|\\*\\/|\\bselect\\b|\\bunion\\b|\\binsert\\b|\\bdrop\\b|\\bdatabase\\b)";
        return texto.matches(".*" + regexSQL + ".*");
    }

    public void updateData(int id, String especialidad, String descripcion) {

        if (!esTextoValido(especialidad) || !esTextoValido(descripcion)) {
            return; // Si los datos no son válidos, no hacer nada
        }

        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/" + id + "/";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("especialidad", especialidad);
            jsonBody.put("descripcion", descripcion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, jsonBody,
                response -> {fetchData();},
                error -> Log.e("Volley", "Error: " + error.getMessage())
        );

        queue.add(jsonObjectRequest);

    }


    public void deleteData(int id) {

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("¿Desea eliminar la Especialidad?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/" + id + "/";
                            RequestQueue queue = Volley.newRequestQueue(getContext());

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                                    response ->{fetchData();} ,
                                    error -> Log.e("Volley", "Error: " + error.getMessage())
                            );

                            queue.add(jsonObjectRequest);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();




    }

}