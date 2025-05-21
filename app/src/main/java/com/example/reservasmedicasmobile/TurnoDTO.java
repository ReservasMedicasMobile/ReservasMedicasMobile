package com.example.reservasmedicasmobile;

public class TurnoDTO {
    private String fecha;
    private String medico;
    private String hora;
    private String especialidad;
    private String nombre_especialidad;
    private String nombre_profesional;
    private String paciente;



    // Constructor
    public TurnoDTO(String fecha, String medico, String hora, String especialidad,String nombre_profesional, String paciente, String nombre_especialidad) {
        this.fecha = fecha;
        this.medico = medico;
        this.hora = hora;
        this.especialidad = especialidad;
        this.nombre_especialidad = nombre_especialidad;
        this.nombre_profesional = nombre_profesional;
        this.paciente = paciente;
    }

    // Getters
    public String getFecha() {
        return fecha;
    }

    public String getMedico() {
        return medico;
    }

    public String getHora() {
        return hora;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getNombre_especialidad() {
        return nombre_especialidad;
    }

    public String getNombre_profesional() {
        return nombre_profesional;
    }

    public String getPaciente() {
        return paciente;
    }
}
