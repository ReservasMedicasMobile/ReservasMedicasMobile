package com.example.reservasmedicasmobile.modelo;

public class DataModel {


    private String id;
    private String especialidad;
    private String descripcion;


    public DataModel(String id, String especialidad, String descripcion) {
        this.id = id;
        this.especialidad = especialidad;
        this.descripcion = descripcion;
    }



    public String getId() {
        return id;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
