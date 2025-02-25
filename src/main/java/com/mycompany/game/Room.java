package com.mycompany.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Room {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private Map<ClientHandler, Snake> snakes = new HashMap<>();
    public Set<ClientHandler> clients = new HashSet<>();
    private int difficulty ;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    public Map<ClientHandler, Snake> getSnakes() {
        return snakes;
    }

    public void setSnakes(Map<ClientHandler, Snake> snakes) {
        this.snakes = snakes;
    }

    public Set<ClientHandler> getClients() {
        return clients;
    }

    public void setClients(Set<ClientHandler> clients) {
        this.clients = clients;
    }
    private boolean gameStarted = false;
    
    public Room(String name) {
        System.out.println("Ingreso en Room");
        this.name = name;
    }

    public void addClient(ClientHandler clientHandler) {
        System.out.println("Ingreso en addClient clase  Room");
        clients.add(clientHandler);
        
        if (gameStarted) {
            startGameForClient(clientHandler);
        }
        sendUserList();
    }

    public void removeClient(ClientHandler clientHandler) {
        System.out.println("Ingreso en removeClient clase  Room");
        clients.remove(clientHandler);
        sendUserList();
    }

    public void broadcast(String message) {
        System.out.println("Ingreso en Broadcast clase  Room");
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void sendUserList() {
        System.out.println("Ingreso en SendUserList clase  Room");
        StringBuilder userList = new StringBuilder("USER_LIST:");
        for (ClientHandler client : clients) {
            userList.append(client.getPlayerName()).append(",");
        }
        broadcast(userList.toString());
    }

    public void startGame() {
        System.out.println("Ingreso en Start Game clase  Room");
        gameStarted = true;
        for (ClientHandler client : clients) {
            snakes.put(client, new Snake());
            client.sendMessage("GAME_STARTED");
        }
        broadcastSnakePositions();
    }

    public void startGameForClient(ClientHandler clientHandler) {
        System.out.println("Ingreso en startGameForClient clase  Room");
        snakes.put(clientHandler, new Snake());
        clientHandler.sendMessage("GAME_STARTED");
    }

    public void updateSnakePosition(ClientHandler clientHandler, String moveInfo) {
        System.out.println("Ingreso en updateSnakePosition clase  Room");
        Snake snake = snakes.get(clientHandler);
        if (snake != null) {
            snake.move();
            broadcastSnakePositions();
        }
    }

    public void broadcastSnakePositions() {
        System.out.println("Ingreso en broadcastSnakePositions clase  Room");

        StringBuilder snakePositions = new StringBuilder("SNAKE_POSITIONS:");
        for (Map.Entry<ClientHandler, Snake> entry : snakes.entrySet()) {
            snakePositions.append(entry.getKey().getPlayerName()).append(":")
                          .append(entry.getValue().getPosition()).append(";");
        }
        broadcast(snakePositions.toString());
    }

    public String getAddress() {
             System.out.println("Ingreso en getAddress clase  Room");
        return name;
    }
}
