package com.proyectogrado.proyectorobotserver.entity;

import com.proyectogrado.proyectorobotserver.util.Constantes;

import java.util.ArrayList;
import java.util.List;

public class Instruccion {

    private String instruccion;

    private String args;

    private List<String> argsList;

    public Instruccion(String instruccion,String args){
        this.instruccion = instruccion;
        this.args = args;
    }

    public Instruccion(String instruccion,List<String> argsList){
        this.instruccion = instruccion;
        this.argsList = argsList;
    }

    public Instruccion(){}

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public List<String> getArgsList() {
        return argsList;
    }

    public void setArgsList(List<String> argsList) {
        this.argsList = argsList;
    }


    @Override
    public String toString(){
        String cadena = getInstruccion() + Constantes.REGEX_SEPARATOR + getArgs() + "\n";
        return cadena;
    }

    public String toStringList(){
        String listaFormated = "";
        for(String cadena : getArgsList()){
            listaFormated = listaFormated + cadena + Constantes.REGEX_DIV;
        }
        String cadena = getInstruccion() + Constantes.REGEX_SEPARATOR + listaFormated + "\n";
        return cadena;
    }

    public static boolean isInstruccion(String instruccion){
        if(instruccion.matches("^[A-Z]+[:]+([a-zA-Z0-9]*+[;]?)*")){
            return true;
        }
        return false;
    }

    public static Instruccion getInstruccion(String linea){
        Instruccion instruccion = new Instruccion();
        String[] instruccionSplit = linea.split(Constantes.REGEX_SEPARATOR);
        instruccion.setInstruccion(instruccionSplit[0]);
        if(instruccionSplit[1].contains(Constantes.REGEX_DIV)){
            String[] argsList = instruccionSplit[1].split(Constantes.REGEX_DIV);
            List<String> listArgs = new ArrayList<>();
            for(String cadena : argsList){
                listArgs.add(cadena);
            }
            instruccion.setArgsList(listArgs);
        }else{
            instruccion.setArgs(instruccionSplit[1]);
        }
        return instruccion;
    }
}
