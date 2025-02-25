package com.mycompany.game;

import java.io.*;
import java.net.*;
import java.util.*;



public class RoomServer {
    private static final int PORT = 54321;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
                                        System.out.println("Ingreso en RoomServer");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Room server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Ingreso en ClientHandler private de Room Server");

            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnections();
            }
        }

        private void broadcast(String message) {
                        System.out.println("Ingreso en broadcast de Client Handler private de Room Server");

            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println(message);
                }
            }
        }

        private void closeConnections() {
                                 System.out.println("Ingreso en closeConnections de Client Handler private de Room Server");

            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
        }
    }
}