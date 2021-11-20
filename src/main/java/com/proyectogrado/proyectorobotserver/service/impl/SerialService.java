package com.proyectogrado.proyectorobotserver.service.impl;

import com.proyectogrado.proyectorobotserver.entity.Instruccion;
import com.proyectogrado.proyectorobotserver.entity.PortsSearch;
import com.proyectogrado.proyectorobotserver.util.Constantes;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class SerialService {

    private Enumeration<CommPortIdentifier> ports;
    private HashMap<String, CommPortIdentifier> portMap = new HashMap<>();
    private boolean connected;
    private CommPortIdentifier selectedPortIdentifier;
    private SerialPort serialPort;
    private InputStream input;
    private OutputStream output;


    public PortsSearch searchForPorts() {
        PortsSearch portsSearch = new PortsSearch();
        List<String> puertos = new ArrayList<>();
        try {
            ports = CommPortIdentifier.getPortIdentifiers();
            while (ports.hasMoreElements()) {
                CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();

                if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    puertos.add(curPort.getName());
                    portMap.put(curPort.getName(), curPort);
                }
            }
            portsSearch.setPorts(puertos);
        }catch (Exception e){
            Instruccion instruccion = new Instruccion(Constantes.ERROR,"No se han podido encontrar ningun puerto");
            portsSearch.setInstruccion(instruccion);
        }
        return portsSearch;
    }

    /**
     * Conecta al puerto pasado por parametro
     *
     * @param puerto
     */
    public void connect(String puerto) {
        if (portMap != null || portMap.size() != 0) {
            selectedPortIdentifier = (CommPortIdentifier) portMap.get(puerto);

            CommPort commPort = null;

            try {
                commPort = selectedPortIdentifier.open("",1000);
                serialPort = (SerialPort) commPort;
                setSerialPortParameters();
                connected = true;

                System.out.println("conectado exitosamente a puerto " + puerto);
            } catch (PortInUseException e) {
                System.out.println("Puerto en uso.");
            } catch (Exception e) {
                System.out.println("Error al abrir puerto.");
            }
        } else {
            System.out.println("No existen puerto o no se han encontrado");
        }
    }

    /**
     * Establece los parametros del serialPort
     *
     * @throws IOException
     */
    private void setSerialPortParameters() throws IOException {
        int baudRate = 4800;

        try {
            serialPort.setSerialPortParams(baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_NONE);
        } catch (UnsupportedCommOperationException ex) {
            throw new IOException("Unsupported serial port parameter");
        }
    }

    /**
     * Comprueba si se puede abrir las comunicaciones
     *
     * @return
     */
    public boolean initIOStream() {
        boolean successful = false;
        try {
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            successful = true;
            return successful;
        } catch (IOException e) {
            System.out.println("Error al abrir Stream.");
            return successful;
        }
    }

    /**
     * Manda la cadena al puerto serial
     *
     * @param aenviar
     */
    public void writeData(String aenviar) {
        try {
            output.write(aenviar.getBytes());
        } catch (IOException ex) {
            System.out.println("Error al enviar informacion.");
        }
    }

    /**
     * Cierra todas las comunicaciones
     */
    public void disconnect() {
        try {
            if (serialPort != null) {
                serialPort.close();
                input.close();
                output.close();
                connected = false;
                System.out.println("Desconectado.");
            }
        } catch (Exception e) {
            System.out.println("Error al desconectar.");
        }
    }

    public boolean isConnected() {
        return connected;
    }

}
