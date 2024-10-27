package com.example.reservasmedicasmobile.modelo;

public class DataModelTurnos {

    private String paciente_id;
    private String profecional_id;
    String hora_turno;
    String fecha_turno;
    String especialidad;

    public DataModelTurnos(String paciente_id, String profecional_id, String hora_turno, String fecha_turno, String especialidad) {
        this.paciente_id = paciente_id;
        this.profecional_id = profecional_id;
        this.hora_turno = hora_turno;
        this.fecha_turno = fecha_turno;
        this.especialidad = especialidad;
    }

    public String getPaciente_id() {
        return paciente_id;
    }

    public String getEspecialidad() {
        return especialidad;
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
