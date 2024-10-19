package com.example.reservasmedicasmobile;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class AgregarEspecialidadFragment extends Fragment {


    private EditText editTextEspcialidad;
    private EditText editTextDescripcion;
    private Button buttonEnviarEspecialidad;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_agregar_especialidad, container, false);

        editTextEspcialidad = view.findViewById(R.id.editTextEspcialidad);
        editTextDescripcion = view.findViewById(R.id.editTextDecripcion);
        buttonEnviarEspecialidad = view.findViewById(R.id.buttonEnviarEspecialidad);

        buttonEnviarEspecialidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEnvio();
            }
        });
        return view;
    }

    private void confirmarEnvio() {
        if (TextUtils.isEmpty(editTextEspcialidad.getText()) || TextUtils.isEmpty(editTextDescripcion.getText())) {
            Toast.makeText(getActivity(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres enviar los datos?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enviarDatos();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void enviarDatos() {
        String url = "https://reservasmedicas.ddns.net/api/v1/especialidad/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Limpiar los campos después de enviar
                        editTextEspcialidad.setText("");
                        editTextDescripcion.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("especialidad", editTextEspcialidad.getText().toString());
                params.put("descripcion", editTextDescripcion.getText().toString());
                return params;
            }
        };


        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }


}