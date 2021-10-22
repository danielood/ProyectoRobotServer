package com.proyectogrado.proyectorobotserver.service.impl;

import com.proyectogrado.proyectorobotserver.service.SocketService;
import com.proyectogrado.proyectorobotserver.util.ConexionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServiceImpl implements SocketService {

    private Socket socket;
    private ServerSocket serverSocket;

    /**
     * Se inicia el socket y se trabaja las conexiones
     */
    @Override
    public void initSocket() {
        try {
            serverSocket = new ServerSocket(ConexionUtil.CON_PORT);
            socket = new Socket();
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Conexion acceptada");
                OutputStream salidaClinte = socket.getOutputStream();
                InputStream salidaServer = socket.getInputStream();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Se cierrra el socket
            closeSocket(socket);
        }
    }

    /**
     * Metodo que obtiene un socket y lo cierra
     * @param socket
     */
    private void closeSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
