package com.example.reservasmedicasmobile;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
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
        final String especialidad = editTextEspcialidad.getText().toString();
        final String descripcion = editTextDescripcion.getText().toString();

        if (especialidad.length() < 4 || especialidad.length() > 25) {
            Toast.makeText(getActivity(), "La especialidad debe tener al menos 4 y un maximo de 25 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descripcion.length() < 4 || descripcion.length() > 100) {
            Toast.makeText(getActivity(), "La descripción debe tener al menos 4 y un maximo de 100 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contieneCaracteresEspeciales(especialidad) || contieneCaracteresEspeciales(descripcion)) {
            Toast.makeText(getActivity(), "El texto no debe contener caracteres especiales.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contieneURL(especialidad) || contieneURL(descripcion)) {
            Toast.makeText(getActivity(), "El texto no debe contener URLs.", Toast.LENGTH_SHORT).show();
            return;
        }

        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que quieres enviar los datos?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        verificarExistenciaEspecialidad(especialidad);;
                    }
                })
                .setNegativeButton("No", null)
                .show();
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

    private void verificarExistenciaEspecialidad(final String especialidad) {
        String url = "https://reservasmedicas.ddns.net/especialidad/existe/" + especialidad;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("\"existe\":true")) {
                            Toast.makeText(getActivity(), "La especialidad ya existe.", Toast.LENGTH_SHORT).show();
                        } else {
                            enviarDatos();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error al verificar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(requireContext()).add(stringRequest);
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