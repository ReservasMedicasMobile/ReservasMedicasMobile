package com.example.reservasmedicasmobile.interfaces;
import com.example.reservasmedicasmobile.modelo.DataModel;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;
public interface ApiService {

    @GET("https://reservasmedicas.ddns.net/api/v1/especialidad/")
    Call<List<DataModel>> getId();
    Call<List<DataModel>> getEspecialidad();
    Call<List<DataModel>> getDescripcion();

}
