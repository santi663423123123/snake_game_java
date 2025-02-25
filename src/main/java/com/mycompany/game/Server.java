package com.mycompany.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;

public class Server {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static Map<String, Client > clients = new ConcurrentHashMap<>();
    private static Map<String, Integer> clientFoodCounts = new ConcurrentHashMap<>();
    private static Map<String, Snake> snakes = new ConcurrentHashMap<>(); // Mapear nombres de jugadores a sus serpientes
    private static Map<String, Integer> customMap = new ConcurrentHashMap<>();

    private static Map<String, Client > client_concurrent = new ConcurrentHashMap<>();
    private static Map<String, CollisionInfo> collisionMap = new ConcurrentHashMap<>();


    static Map<Snake, GamePanel> snakeGamePanelMap = new HashMap<>(); // Nuevo mapa para guardar las asociaciones de Snake y GamePanel

    
    
    /*
    public static void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started on port " + PORT);
                
                while (true) {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }*/
    
    
            public static boolean startServer() {
              final boolean[] started = {false}; // Use an array to hold the state
               Thread serverThread = new Thread(() -> {
               try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                   System.out.println("Server started on port " + PORT);
                   started[0] = true; // Update the state

                   while (true) {
                       Socket socket = serverSocket.accept();
                       ClientHandler clientHandler = new ClientHandler(socket);

                       clientHandlers.add(clientHandler);
                       new Thread(clientHandler).start();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           });

           serverThread.start();

           // Wait for a short time to confirm server start, then return the state
           try {
               Thread.sleep(100); // Adjust the sleep time as necessary
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           return started[0];
       }

    
    
    
    public static void addClient( String nombre , Client cliente) {
        // Crear un identificador único para cada cliente
        clients.put(nombre , cliente);
        System.out.println("Client added: " + nombre);
        
    }
        public static Client getClient(String clientId) {
        return clients.get(clientId);
    }
    public static void sendMessageToAll(String message) {
        broadcast(message, null);  // Broadcast message to all clients without excluding anyone
    }

    public static void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != excludeUser) {
                clientHandler.sendMessage(message);
            }
        }
    }
    // Método para eliminar un cliente por su ID
    public static void removeClient(String clientId) {
        if (clients.containsKey(clientId)) {
            clients.remove(clientId);
            System.out.println("Client removed: " + clientId);
        }
    }
    
    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static void addFood(String clientId, int foodToAdd) {
    clientFoodCounts.merge(clientId, foodToAdd, Integer::sum);
    System.out.println("Updated food count for " + clientId + ": " + clientId);
    }

    public static synchronized int getFood(String clientId) {
        return clientFoodCounts.getOrDefault(clientId, 0);
    }
    public static String getWinner() {
        return Collections.max(clientFoodCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
    public static void updateCustomMap(String key, int value) {
        customMap.put(key, value);
        System.out.println("Custom map updated: " + key + " -> " + value);
    }
    public static int getCustomMapValue(String key) {
        return customMap.getOrDefault(key, 0);
    }
    public static Map<String, Integer> getCustomMap() {
        return new ConcurrentHashMap<>(customMap);
    }
    public static void createRoom(String roomName, ClientHandler clientHandler, String difficulty) {
        if (!rooms.containsKey(roomName)) {
            Room room = new Room(roomName);
            room.addClient(clientHandler);

            try {
                int difficultyLevel = Integer.parseInt(difficulty);  // Convertir difficulty a int
                room.setDifficulty(difficultyLevel);                 // Asumiendo que setDifficulty acepta un int
            } catch (NumberFormatException e) {
                System.out.println("Error: la dificultad proporcionada no es un número válido.");
                clientHandler.sendMessage("INVALID_DIFFICULTY");
                return; // Termina la ejecución del método si la conversión falla
            }

            rooms.put(roomName, room );
            System.out.println("ROOM_CREATED Class Server ");
            clientHandler.sendMessage("ROOM_CREATED:" + roomName);
        } else {
            System.out.println("createRoom Class Server ");
            clientHandler.sendMessage("ROOM_EXISTS");
        }
    }
    public static  void setFood(String clientId, int newFoodCount) {
        clientFoodCounts.put(clientId, newFoodCount);
        System.out.println("Food count set for " + clientId + ": " + newFoodCount);
    }

    public static void displayFoodCounts() {
        if (clientFoodCounts.isEmpty()) {
            System.out.println("No food counts to display.");
        } else {
            System.out.println("Current Food Counts:");
            clientFoodCounts.forEach((clientName, foodCount) -> {
                System.out.println("Client: " + clientName + " - Food Count: " + foodCount);
            });
        }
    }
       public static Map<String, Integer> getAllFoodCounts() {
        // Crea un nuevo mapa para evitar modificaciones no sincronizadas en el mapa original desde otros hilos
        return new ConcurrentHashMap<>(clientFoodCounts);
    }
       public static Map<String, Snake> getAllSnakePositions() {
        // Crea un nuevo mapa para evitar modificaciones no sincronizadas en el mapa original desde otros hilos
        return new ConcurrentHashMap<>(snakes);
    }
    public static void joinRoom(String roomName, ClientHandler clientHandler, String difficulty) {
        Room room = rooms.get(roomName);
        if (room != null) {
            room.addClient(clientHandler);
            try {
                int diff = Integer.parseInt(difficulty);
                room.setDifficulty(diff);  // Ajustar la dificultad de la sala
            } catch (NumberFormatException e) {
                clientHandler.sendMessage("INVALID_DIFFICULTY");
                return;
            }
            clientHandler.sendMessage("ROOM_JOINED:" + roomName);
            int dif = getCustomMapValue(roomName);
            room.broadcast("USER_JOINED:" + clientHandler.getSocket().getInetAddress() + ":" + dif );
            room.sendUserList();
        } else {
            clientHandler.sendMessage("ROOM_NOT_FOUND");
        }

        //sendMessageToAll("Salas Disponibles:" + processAllRooms());
    }

    public static void startGame(String roomName) {
        System.out.println("Clase Server  metodo startGame");
        Room room = rooms.get(roomName);
        if (room != null) {
            room.startGame();
        }
    }
    public static Room getRoom(String roomName) {
        return rooms.get(roomName);
    }


    // Función para registrar un GamePanel con una Snake
    public static void registerSnakeGamePanel(Snake snake, GamePanel gamePanel) {
        System.out.println("Clase Server  metodo registerSnakeGamePanel");

        snakeGamePanelMap.put(snake, gamePanel);
    }

    // Función para obtener un GamePanel a partir de una Snake
    public static GamePanel getGamePanel(Snake snake) {
        System.out.println("Clase Server  metodo getGamePanel");
        return snakeGamePanelMap.get(snake);
    }

    static void updateSankesMovement(String part1 , String part2) {
        //System.out.println("LLegó de snakes movement: " + part);
       // sendMessageToAll("SNAKE_POSITIONS:" + part1 + '{' + part2 + '}' );
    }
    
    
    
    public static String processAllRooms() {
        StringBuilder sb = new StringBuilder();
        sb.append("Room Summary:\n");
        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            String roomName = entry.getKey();
            Room room = entry.getValue();
            sb.append("Room Name: ").append(roomName)
              .append(", Number of Clients: ").append(room.getClients().size())
              .append(", Difficulty: ").append(room.getDifficulty()).append("\n");
        }
        return sb.toString();
    }

    
       public static void updateSnakePositions(String playerName, String positions) {
       /* // Obtiene la serpiente asociada al jugador
        Snake snake = snakes.get(playerName);
        
        if (snake != null) {
            // Actualiza la posición de la serpiente usando el método adecuado
            snake.setPositionFromString(positions);
            System.out.println("Updated snake position for player: " + playerName);
        } else {
            // Si no existe la serpiente, crea una nueva y la agrega al mapa
            snake = new Snake();
            snake.setPositionFromString(positions);
            snakes.put(playerName, snake);
            System.out.println("New snake created and position set for player: " + playerName + "positions " + positions);
        }
        
        sendMessageToAll("SNAKE_POSITIONS:" +getAllSnakePositions());
        displaySnakePositions()
           ;*/
    }
        
    public static void displaySnakePositions() {
        if (snakes.isEmpty()) {
            System.out.println("No snake positions to display.");
        } else {
            System.out.println("Current Snake Positions:");
            snakes.forEach((playerName, snake) -> {
                // Obtiene la posición de la serpiente como una cadena de texto
                String position = snake.getPosition();
                System.out.println("Player: " + playerName + " - Position: " + position);
            });
        }
    }


    public static void displayAllRoomsAndClients() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms to display.");
        } else {
            System.out.println("Rooms and their clients:");
            for (Map.Entry<String, Room> entry : rooms.entrySet()) {
                String roomName = entry.getKey();
                Room room = entry.getValue();
                System.out.println("Room Name: " + roomName + ", Difficulty: " + room.getDifficulty() + ", Number of Clients: " + room.getClients().size());

                for (ClientHandler clientHandler : room.getClients()) {
                    System.out.println("    Client: " + clientHandler.getPlayerName());
                }
            }
        }
    }

    private static void checkAllCollisions(String roomName) {
        Room room = rooms.get(roomName);
        if (room != null) {
            boolean allCollided = true;
            for (ClientHandler clientHandler : room.getClients()) {
                if (!collisionMap.containsKey(clientHandler.getPlayerName())) {
                    allCollided = false;
                    break;
                }
            }

            if (allCollided) {
                // Determine the rankings based on the food count
                List<Map.Entry<String, Integer>> rankings = determineRankings(room);

                // Crear un mensaje que incluya el ganador y los lugares
                StringBuilder winnerMessage = new StringBuilder("WINNER:");
                for (int i = 0; i < rankings.size(); i++) {
                    Map.Entry<String, Integer> entry = rankings.get(i);
                    winnerMessage.append(entry.getKey()).append(":").append(i + 1); // i + 1 da la posición correcta
                    if (i < rankings.size() - 1) {
                        winnerMessage.append(",");
                    }
                }

                sendMessageToAll(winnerMessage.toString());
                System.out.println("Winner and rankings for room " + roomName + ": " + winnerMessage);
            }
        }
    }

    private static List<Map.Entry<String, Integer>> determineRankings(Room room) {
        Map<String, Integer> foodCounts = new HashMap<>();
        for (ClientHandler clientHandler : room.getClients()) {
            String playerName = clientHandler.getPlayerName();
            int foodCount = clientFoodCounts.getOrDefault(playerName, 0);
            foodCounts.put(playerName, foodCount);
        }

        // Ordenar la lista de jugadores por su puntuación de mayor a menor
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(foodCounts.entrySet());
        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        return sortedList;
    }
    private static String determineWinner(Room room) {
        String winner = null;
        int maxFoodCount = -1;
        for (ClientHandler clientHandler : room.getClients()) {
            String playerName = clientHandler.getPlayerName();
            int foodCount = clientFoodCounts.getOrDefault(playerName, 0);
            if (foodCount > maxFoodCount) {
                maxFoodCount = foodCount;
                winner = playerName;
            }
        }
        return winner;
    }
        public static void ColisionNotify(String clientPlayerName, String roomName, String secondsPlayedStr, String fruitCountStr) {
            int secondsPlayed;
            int fruitCount;

            try {
                secondsPlayed = Integer.parseInt(secondsPlayedStr);
                fruitCount = Integer.parseInt(fruitCountStr);
            } catch (NumberFormatException e) {
                System.out.println("Error: secondsPlayed or fruitCount is not a valid integer");
                return; // Exit the method if the conversion fails
            }

            Room room = rooms.get(roomName);
            if (room != null) {
                for (ClientHandler clientHandler : room.getClients()) {
                    if (clientHandler.getPlayerName().equals(clientPlayerName)) {
                        CollisionInfo collisionInfo = new CollisionInfo(clientPlayerName, roomName, secondsPlayed, fruitCount);
                        collisionMap.put(clientPlayerName, collisionInfo);
                        System.out.println("Collision recorded: " + collisionInfo);

                        // Send collision message to all clients
                        String colisionMessage = String.format("COLISION:%s:%s:%d:%d",
                                                                clientPlayerName,
                                                                roomName,
                                                                secondsPlayed,
                                                                fruitCount);
                        sendMessageToAll(colisionMessage);
                        break;
                    }
                }

                // Check if all players in the room have collided
                checkAllCollisions(roomName);
            } else {
                System.out.println("Room not found: " + roomName);
            }
        }

        
        
        
public static void closeClientConnection(String clientId) {
    // Obtiene el cliente primero
    Client client = clients.get(clientId);
    if (client == null) {
        System.out.println("Client " + clientId + " not found.");
        return;
    }

    // Obtiene el ClientHandler desde el conjunto de manejadores
    ClientHandler handlerToRemove = null;
    for (ClientHandler handler : clientHandlers) {
        if (handler.getPlayerName().equals(clientId)) {
            handlerToRemove = handler;
            break;
        }
    }

    if (handlerToRemove != null) {

            // Cierra el socket
           // handlerToRemove.closeConnections(); // Asume que tienes un método closeConnection en ClientHandler
            // Elimina el ClientHandler de las colecciones
            clientHandlers.remove(handlerToRemove);
            System.out.println("Connection and handler removed for client: " + clientId);
        
    } else {
        System.out.println("No active handler found for client: " + clientId);
    }

    // Finalmente, elimina las referencias del cliente
    clients.remove(clientId);
    clientFoodCounts.remove(clientId);
    snakes.remove(clientId);

}
    static void EnvioDeRoomUsers(String mensaje){
    sendMessageToAll("EnvioDeRoomUsers:" + Server.getAllFoodCounts());
    }
static void updateScoreBoard(String part, String part0, String part1, String part3, String part7, String part9) {
            System.out.println("Llego:" + part + " " + "part1 " + part1 + " part3 " + part3 + "part7 " + part7 + " " + part9);
            int part9Int;
            try {
                part9Int = Integer.parseInt(part9);
            } catch (NumberFormatException e) {
                System.out.println("Error: part9 is not a valid integer");
                return; // Exit the method if the conversion fails
            }
            Server.setFood(part3, part9Int);            
            Client cl = Server.getClient(part3);
            
            /*
            if (cl != null) {
                GamePanel gp = cl.getGamePanel();
                if (gp != null) {
                        System.out.println("gp no es null para " + part3);
                        gp.updateOtherPlayerFruitCounts(Server.getAllFoodCounts());
                        gp.repaint();
                   
                }else {
                       System.out.println("gp es null para + " + part3);
                       
                }
            }else{
                 System.out.println("gp es null para + " + part3);

            }*/
            
            System.out.println("Food count updated for: " + part3);
            displayFoodCounts();

            sendMessageToAll("Este es un mensaje para todos:" + Server.getAllFoodCounts());
            
           
  
                
    /*
    for (Map.Entry<String, Room> entry : rooms.entrySet()) {
        String roomId = entry.getKey(); // Identifier of the room
        Room room = entry.getValue();   // Instance of the room
        
        System.out.println("Processing the room with ID: " + roomId);
        System.out.println("Clients in the room: " + room.getClients().size());
       
        Map<ClientHandler, Snake> snakes = room.getSnakes();

        processFoodCounts
        for (Map.Entry<ClientHandler, Snake> snakeEntry : snakes.entrySet()) {
            ClientHandler clientHandler = snakeEntry.getKey();
            Snake snake = snakeEntry.getValue();
            System.out.println("Client: " + clientHandler.getPlayerName());

            if (Server.getClient(clientHandler.getPlayerName()) != null){
                System.out.println("Reference to Client exists: " + Server.getClient(clientHandler.getPlayerName()));
                Client cl2 = Server.getClient(clientHandler.getPlayerName());
                GamePanel a = cl2.getGamePanel();

                // Correct call to add food to the food counts
                Server.addFood(clientHandler.getPlayerName(), a.getFruitCount());
                System.out.println("Food count updated for: " + clientHandler.getPlayerName());
                displayFoodCounts();
            } else {
                System.out.println("No reference to Client");
            }
        }
        
    }
        */
}

}

