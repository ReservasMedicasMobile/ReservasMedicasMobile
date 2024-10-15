package com.example.reservasmedicasmobile.modelo;

public class DataModel {

    private String id;
    private String especiaslidad;
    private String descripcion;


    public DataModel(String id) {
        this.id = id;
        this.especiaslidad = especiaslidad;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public String getEspeciaslidad() {
        return especiaslidad;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
