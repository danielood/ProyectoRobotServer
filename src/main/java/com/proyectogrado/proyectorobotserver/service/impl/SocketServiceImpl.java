package com.proyectogrado.proyectorobotserver.service.impl;

import com.proyectogrado.proyectorobotserver.service.SocketService;
import com.proyectogrado.proyectorobotserver.util.ConexionUtil;

import java.io.*;
import java.net.InetSocketAddress;
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
        try (ServerSocket serverSocket = new ServerSocket()) {
            InetSocketAddress addr = new InetSocketAddress(ConexionUtil.CON_ADDRESS,ConexionUtil.CON_PORT);
            serverSocket.bind(addr);
            try(Socket newSocket = serverSocket.accept();
                InputStream is = newSocket.getInputStream();
                OutputStream out = newSocket.getOutputStream();
                InputStreamReader isr = new InputStreamReader(is);
                OutputStreamWriter osw = new OutputStreamWriter(out);
                BufferedReader bReader = new BufferedReader(isr);
                PrintWriter printWriter = new PrintWriter(osw);){
                System.out.println("Conexion recibida");
                String mensaje = bReader.readLine();
                System.out.println("Mensaje recibido: " + mensaje);

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
