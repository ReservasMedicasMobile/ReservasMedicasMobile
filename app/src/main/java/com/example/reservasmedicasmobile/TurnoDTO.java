package com.example.reservasmedicasmobile;

public class TurnoDTO {
    private String fecha;
    private String medico;

    public TurnoDTO(String fecha, String medico) {
        this.fecha = fecha;
        this.medico = medico;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMedico() {
        return medico;
    }
}
