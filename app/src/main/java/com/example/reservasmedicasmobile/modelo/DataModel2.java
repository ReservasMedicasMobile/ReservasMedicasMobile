package com.example.reservasmedicasmobile.modelo;

public class DataModel2 {

    private int id;
    private String especialidad;
    private String descripcion;

    public DataModel2(int id, String especialidad, String descripcion) {
        this.id = id;
        this.especialidad = especialidad;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }



    public String getEspecialidad() {
        return especialidad;
    }



    public String getDescripcion() {
        return descripcion;
    }


}
