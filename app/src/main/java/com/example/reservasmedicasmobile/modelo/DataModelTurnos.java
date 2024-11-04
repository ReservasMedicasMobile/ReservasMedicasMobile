package com.example.reservasmedicasmobile.modelo;

public class DataModelTurnos {

    private int id;
    private String paciente_id;
    private String profecional_id;
    String hora_turno;
    String fecha_turno;
    int especialidad;
    boolean cancelado;


    public DataModelTurnos(int id,boolean cancelado,String paciente_id, String profecional_id, String hora_turno, String fecha_turno, int especialidad) {
        this.paciente_id = paciente_id;
        this.profecional_id = profecional_id;
        this.hora_turno = hora_turno;
        this.fecha_turno = fecha_turno;
        this.especialidad = especialidad;
        this.cancelado = cancelado;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public int getEspecialidad() {
        return especialidad;
    }

    public String getPaciente_id() {
        return paciente_id;
    }




    public String getHora_turno() {
        return hora_turno;
    }

    public String getFecha_turno() {
        return fecha_turno;
    }

    public String getProfecional_id() {
        return profecional_id;
    }
}
