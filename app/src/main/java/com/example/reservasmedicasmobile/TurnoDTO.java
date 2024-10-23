package com.example.reservasmedicasmobile;

public class TurnoDTO {
    private String fecha;
    private String medico;
    private String hora;
    private String especialidad;
    private String paciente;

    // Constructor
    public TurnoDTO(String fecha, String medico, String hora, String especialidad, String paciente) {
        this.fecha = fecha;
        this.medico = medico;
        this.hora = hora;
        this.especialidad = especialidad;
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

    public String getPaciente() {
        return paciente;
    }
}
