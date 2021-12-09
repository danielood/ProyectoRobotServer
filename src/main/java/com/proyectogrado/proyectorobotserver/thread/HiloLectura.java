package com.proyectogrado.proyectorobotserver.thread;

import com.proyectogrado.proyectorobotserver.entity.Instruccion;
import com.proyectogrado.proyectorobotserver.service.impl.SocketServiceImpl;
import com.proyectogrado.proyectorobotserver.util.Constantes;

import java.io.BufferedReader;
import java.io.IOException;

public class HiloLectura extends Thread {

    private BufferedReader bufferedReader;
    private boolean live;
    private SocketServiceImpl socketService;

    public HiloLectura(BufferedReader bufferedReader, SocketServiceImpl socketService) {
        this.bufferedReader = bufferedReader;
        this.socketService = socketService;
        live = true;
    }

    @Override
    public void run() {
        while (live) {
            try {
                if (bufferedReader.ready()) {
                    String cadena = bufferedReader.readLine();
                    if (Instruccion.isInstruccion(cadena)) {
                        Instruccion instruccion = Instruccion.getInstruccion(cadena);
                        readInstruccion(instruccion);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readInstruccion(Instruccion instruccion) {
        if (instruccion.getInstruccion().equals(Constantes.CONNECT_PORT)) {
            socketService.sendConnectPort(instruccion);
        } else if (instruccion.getInstruccion().equals(Constantes.COMD)) {
            socketService.sendDataSerial(instruccion);
        } else if (instruccion.getInstruccion().equals(Constantes.CLOSE)) {
            socketService.closeSocket();
        } else {
            socketService.sendErrorInstruccion();
        }
    }

    public void destroyHilo() {
        live = false;
    }
}

