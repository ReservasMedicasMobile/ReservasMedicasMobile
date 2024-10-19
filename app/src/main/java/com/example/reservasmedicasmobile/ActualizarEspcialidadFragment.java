package com.example.reservasmedicasmobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actualizar_espcialidad, container, false);
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

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

    public void updateData(int id, String especialidad) {
        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/" + id;
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("especialidad", especialidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> fetchData(),
                error -> Log.e("Volley", "Error: " + error.getMessage())
        );

        queue.add(jsonObjectRequest);
    }

    public void deleteData(int id) {

        String url = "http://127.0.0.1:8000/eliminarespecialidad/" + id + "/";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> fetchData(),
                error -> Log.e("Volley", "Error: " + error.getMessage())
        );

        queue.add(jsonObjectRequest);
    }

}