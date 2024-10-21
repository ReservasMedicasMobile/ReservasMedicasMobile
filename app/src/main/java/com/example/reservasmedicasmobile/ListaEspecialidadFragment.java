package com.example.reservasmedicasmobile;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListaEspecialidadFragment extends Fragment {


    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_especialidad, container, false);
        linearLayout = view.findViewById(R.id.linearLayoutEspecialidad);
        obtenerDatos();
        return view;
    }

    private void obtenerDatos() {
        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mostrarDatos(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );


        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
    }

    private void mostrarDatos(JSONArray DataModel) {
        for (int i = 0; i < DataModel.length(); i++) {
            try {
                JSONObject Especialidad = DataModel.getJSONObject(i);
                String Codigo = Especialidad.getString("id");
                String Nombre = Especialidad.getString("especialidad");
                String Descripcion = Especialidad.getString("descripcion");

                TextView textView = new TextView(getActivity());
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setTextSize(20);
                textView.setPadding(16, 16, 16, 16);
                textView.setText(Codigo + "- ");
                textView.append(Nombre + ": \n");
                textView.append("* "+Descripcion + "\n");
                textView.append("----------------------------------------------------------\n");

                linearLayout.setBackgroundResource(R.color.azul);
                linearLayout.addView(textView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}