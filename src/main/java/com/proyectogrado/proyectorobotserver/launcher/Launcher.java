package com.proyectogrado.proyectorobotserver.launcher;

import com.proyectogrado.proyectorobotserver.service.SocketService;
import com.proyectogrado.proyectorobotserver.service.impl.SocketServiceImpl;

public class Launcher {

    public static void main(String[] args) {
        SocketService socketService = new SocketServiceImpl();
        socketService.initSocket();
    }

}
