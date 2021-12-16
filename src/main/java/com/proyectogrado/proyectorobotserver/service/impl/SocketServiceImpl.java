package com.proyectogrado.proyectorobotserver.service.impl;

import com.proyectogrado.proyectorobotserver.entity.Instruccion;
import com.proyectogrado.proyectorobotserver.entity.PortsSearch;
import com.proyectogrado.proyectorobotserver.service.SocketService;
import com.proyectogrado.proyectorobotserver.thread.HiloLectura;
import com.proyectogrado.proyectorobotserver.util.ConexionUtil;
import com.proyectogrado.proyectorobotserver.util.Constantes;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocketServiceImpl implements SocketService {

    private ServerSocket serverSocket;
    private Socket socket;
    private SerialService serialService;
    private PrintWriter printWriter;
    private BufferedReader bReader;
    private List<String> puertos;
    private HiloLectura hiloLectura;

    public SocketServiceImpl() {
    }

    /**
     * Se inicia el socket y se trabaja las conexiones
     */
    @Override
    public void initSocket() {
        try {
            serverSocket = new ServerSocket();
            serialService = new SerialService();
            InetSocketAddress addr = new InetSocketAddress(ConexionUtil.CON_ADDRESS, ConexionUtil.CON_PORT);
            serverSocket.bind(addr);
            accpetConexion(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendConnectPort(Instruccion instruccion) {
        String puerto = instruccion.getArgs();
        if (!this.puertos.contains(puerto)) {
            sendErrorInstruccion("El puerto no coincide con los del servidor");
        } else {
            connectPort(puerto);
        }
    }

    public void sendErrorInstruccion() {
        Instruccion instruccion = new Instruccion(Constantes.ERROR, "Error al enviar la peticion");
        sendInstruccionArgs(instruccion);
    }

    public void sendErrorInstruccion(String args) {
        Instruccion instruccion = new Instruccion();
        instruccion.setInstruccion(Constantes.ERROR);
        instruccion.setArgs(args);
        sendInstruccionArgs(instruccion);
    }

    public synchronized void sendDataSerial(Instruccion instruccion) {
        if (instruccion.getArgsList() == null) {
            writeDataPort(instruccion.getArgs());
        } else if (instruccion.getArgsList() != null) {
            for (String comando : instruccion.getArgsList()) {
                writeDataPort(comando);
            }
        } else {
            sendErrorInstruccion("No se pueden enviar instrucciones vacias");
        }
    }

    public synchronized void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (bReader != null) {
                bReader.close();
                bReader = null;
            }
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
            serialService.disconnect();
            if (hiloLectura.isAlive()) {
                hiloLectura.destroyHilo();
            }
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }

            initSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accpetConexion(ServerSocket serverSocket) throws IOException {
        try {
            if (socket == null) {
                socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                InputStreamReader isr = new InputStreamReader(is);
                OutputStreamWriter osw = new OutputStreamWriter(out);
                bReader = new BufferedReader(isr);
                printWriter = new PrintWriter(osw);
                hiloLectura = new HiloLectura(bReader, this);
                sendPorts();
                hiloLectura.start();
            } else {
                sendErrorInstruccion("Ya hay una conexion al servidor");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPorts() {
        PortsSearch portsSearch = serialService.searchForPorts();
        if (portsSearch.getPorts() != null) {
            List<String> puertos = portsSearch.getPorts();
            this.puertos = puertos;
            Instruccion instruccion;
            if (puertos.size() == 0) {
                instruccion = new Instruccion(Constantes.ERROR, "No se ha podido encontrar ningun puerto disponible");
                sendInstruccionArgs(instruccion);
            } else {
                instruccion = new Instruccion(Constantes.PORTS, puertos);
                sendInstruccionArgsList(instruccion);
            }
        }
    }

    private void sendInstruccionArgs(Instruccion instruccion) {
        String cadena = instruccion.toString();
        printWriter.write(cadena);
        printWriter.flush();
    }

    private void sendInstruccionArgsList(Instruccion instruccion) {
        String cadena = instruccion.toStringList();
        printWriter.write(cadena);
        printWriter.flush();
    }

    private void connectPort(String port) {
        serialService.connect(port);
        if (serialService.initIOStream() && serialService.isConnected()) {
            Instruccion instruccion = new Instruccion(Constantes.OK, "Se ha conectado correctamente al puerto");
            sendInstruccionArgs(instruccion);
        } else {
            sendErrorInstruccion();
        }
    }

    private void writeDataPort(String comando) {
        serialService.writeData(comando);
    }
}
