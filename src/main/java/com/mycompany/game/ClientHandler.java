package com.mycompany.game;

import java.io.*;
import static java.lang.Thread.currentThread;
import java.net.*;
import java.util.concurrent.Executor;

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String roomName;
    private Client client; // Referencia a Client


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    public String getRoomName() {
        return roomName;
    }
    public String playerName;
    

    public  ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        int i = 0 ;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message + Thread.currentThread() );
                i++ ;

                handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
            Server.removeClient(this);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    
    private void handleMessage(String message) {
        String[] parts = message.split(":");
        String command = parts[0];

        if (parts.length < 2) {
            return;
        }

        String roomName = parts[1];
        switch (command) {
            case "CREATE":
                if (parts.length > 2) {
                    this.playerName = parts[2];
                    Server.createRoom(roomName, this , parts[3]);
                }
                break;
            case "ROOM_EXISTS":
                System.out.println("El room existe");
                break;
            case "JOIN":
                if (parts.length > 2) {
                    this.playerName = parts[2];
                    Server.joinRoom(roomName, this , parts[3]);
                }
        case "SCOREBOARD":
             if (parts.length > 5) {
            // Asegúrate de que todos los elementos necesarios están presentes
               //System.out.println("LLego al otro lado::");
                
                for (int i = 0 ; i < parts.length ; i ++ ){
                System.out.println("parts" + i + " " + parts[i]);

                }
                // Procesar posición de la serpiente aquí
                // parts[1] = playerName, parts[2] = position, parts[3] = roomName
                Server.updateScoreBoard(parts[0], parts[1], parts[2], parts[3] , getRoomName() , parts[5] );
                }
                break;                                              
               case "COLISION":
                for (int i = 0 ; i < parts.length ; i ++ ){
                System.out.println("parts" + i + " " + parts[i]);

                }  
                Server.ColisionNotify(parts[1], parts[2], parts[3], parts[4] );
    
                break;
            case "START":
                Server.startGame(roomName);
                break;
            case "SNAKE_POSITIONS":
                Server.updateSnakePositions(parts[1] , parts[2]);

                break;
            default:
                Server.broadcast(message, this);
                break;
        }
    }

    private void closeConnections() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
