package com.mycompany.game;

import java.io.*;
import java.net.*;
import java.util.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, PrintWriter> clientOutputs; // Guarda los outputs hacia los clientes

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientOutputs = new ConcurrentHashMap<>();
        acceptClients();
    }

    private void acceptClients() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String playerName = in.readLine(); // Lee el nombre de usuario al conectar
                    clientOutputs.put(playerName, out);

                    // Manejar la entrada del cliente en un nuevo hilo
                    new Thread(() -> handleClientInput(playerName, in)).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleClientInput(String playerName, BufferedReader in) {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                processCommand(playerName, inputLine);
            }
        } catch (IOException e) {
            clientOutputs.remove(playerName);
            System.out.println(playerName + " disconnected.");
        }
    }

    private void processCommand(String playerName, String command) {
        // Aquí procesas comandos como movimientos o actualizaciones de estado
        System.out.println("Received from " + playerName + ": " + command);

        // Envía esta información a todos los clientes
        for (PrintWriter out : clientOutputs.values()) {
            out.println(playerName + ":" + command); // Reenvía la información recibida
        }
    }

    public static void main(String[] args) {
        try {
            new GameServer(12345);
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
