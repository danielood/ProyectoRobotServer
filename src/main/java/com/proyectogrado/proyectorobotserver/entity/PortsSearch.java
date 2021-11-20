package com.proyectogrado.proyectorobotserver.entity;

import java.util.List;

public class PortsSearch {

    private List<String> ports;

    private Instruccion instruccion;

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    public Instruccion getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(Instruccion instruccion) {
        this.instruccion = instruccion;
    }
}
