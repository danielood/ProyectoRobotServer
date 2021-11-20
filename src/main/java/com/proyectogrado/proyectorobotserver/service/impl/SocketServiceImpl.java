package com.proyectogrado.proyectorobotserver.service.impl;

import com.proyectogrado.proyectorobotserver.entity.Instruccion;
import com.proyectogrado.proyectorobotserver.entity.PortsSearch;
import com.proyectogrado.proyectorobotserver.service.SocketService;
import com.proyectogrado.proyectorobotserver.util.ConexionUtil;
import com.proyectogrado.proyectorobotserver.util.Constantes;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocketServiceImpl implements SocketService {

    private Socket socket;
    private ServerSocket serverSocket;
    private SerialService serialService;
    private PrintWriter printWriter;
    private BufferedReader bReader;
    private boolean serialPort;
    private boolean dataSend;

    public SocketServiceImpl(){
        serialPort = true;
        dataSend = true;
    }

    /**
     * Se inicia el socket y se trabaja las conexiones
     */
    @Override
    public void initSocket() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            //Traza
            System.out.println("Abriendo el socket del servidor");
            serialService = new SerialService();
            InetSocketAddress addr = new InetSocketAddress(ConexionUtil.CON_ADDRESS, ConexionUtil.CON_PORT);
            serverSocket.bind(addr);
            accpetConexion(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll(socket);
        }
    }

    private void accpetConexion(ServerSocket serverSocket) throws IOException {
        //Traza
        System.out.println("Esperando conexiones");
        try (Socket newSocket = serverSocket.accept();
             InputStream is = newSocket.getInputStream();
             OutputStream out = newSocket.getOutputStream();
             InputStreamReader isr = new InputStreamReader(is);
             OutputStreamWriter osw = new OutputStreamWriter(out)) {
            //Traza
            System.out.println("Conexion aceptada");
            bReader = new BufferedReader(isr);
            printWriter = new PrintWriter(osw);
            sendPorts();
            getSerialPort();
            while(true){
                sendData(newSocket);
            }
        }
    }

    private void getSerialPort() throws IOException {
        while (serialPort) {
            if (bReader.ready()) {
                String linea = bReader.readLine();
                if (!Instruccion.isInstruccion(linea)) {
                    sendErrorInstruccion();
                } else {
                    Instruccion instruccion = Instruccion.getInstruccion(linea);
                    if (instruccion.getInstruccion().equals(Constantes.CONNECT_PORT)) {
                        if (instruccion.getArgsList() == null) {
                            connectPort(instruccion.getArgs());
                        } else {
                            Instruccion instruccionError = new Instruccion(Constantes.ERROR, "Solo se puede enviar un puerto");
                            sendInstruccionArgs(instruccionError);
                        }
                    }
                }
                serialPort = false;
            }
        }
    }

    private void sendPorts() {
        //Traza
        System.out.println("Mandando puertos");
        PortsSearch portsSearch = serialService.searchForPorts();
        if(portsSearch.getPorts()!=null) {
            List<String> puertos = portsSearch.getPorts();
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

    private void sendErrorInstruccion(){
        Instruccion instruccion = new Instruccion(Constantes.ERROR, "Error al enviar la peticion");
        sendInstruccionArgs(instruccion);
    }

    private void connectPort(String port) {
        serialService.connect(port);
        if (serialService.isConnected()) {
            Instruccion instruccion = new Instruccion(Constantes.OK, "Se ha conectado correctamente al puerto");
            sendInstruccionArgs(instruccion);
        }
    }

    private void sendData(Socket socket) throws IOException {
        while(dataSend) {
            if(bReader.ready()) {
                String linea = bReader.readLine();
                if (Instruccion.isInstruccion(linea)) {
                    Instruccion instruccion = Instruccion.getInstruccion(linea);
                    if (instruccion.getInstruccion().equals(Constantes.COMD)) {
                        if (instruccion.getArgsList() == null) {
                            for (String comando : instruccion.getArgsList()) {
                                writeDataPort(comando);
                            }
                        } else {
                            writeDataPort(instruccion.getArgs());
                        }
                    } else if (instruccion.getInstruccion().equals(Constantes.CLOSE)) {
                        socket.close();
                    }
                } else {
                    sendErrorInstruccion();
                }
                dataSend = false;
            }
        }
    }

    private void writeDataPort(String comando){
        serialService.writeData(comando);
    }


    /**
     * Metodo que obtiene un socket y lo cierra
     *
     * @param socket
     */
    private void closeAll(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
                printWriter.close();
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
