package com.mycompany.game;

import java.awt.Point;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private GamePanel gp;
    private int room_dificulty;

    public int getRoom_dificulty() {
        return room_dificulty;
    }

    public void setRoom_dificulty(int room_dificulty) {
        this.room_dificulty = room_dificulty;
    }
    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }
    private BufferedReader in;
    private RoomPanel roomPanel;

    public RoomPanel getRoomPanel() {
        return roomPanel;
    }
    private String PlayerName;

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String PlayerName) {
        this.PlayerName = PlayerName;
    }

    public Client(String serverAddress, int port , String PlayerName) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.PlayerName = PlayerName;
            new Thread(new Listener()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onUserListUpdate(List<String> users) {
        if (roomPanel != null) {
            roomPanel.updateUsers(users);
        }
    }
    public void setRoomPanel(RoomPanel roomPanel) {
        this.roomPanel = roomPanel;
    }
    public GamePanel getGamePanel() {
        return gp;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gp = gamePanel;
    }
    public void sendInput(String input) {
        if (out != null) {
            out.println(input);
        }
    }
    public void sendInput_b( byte[] b) {
        if (out != null) {
            out.println(b);
        }
    }
    public void createRoom(String roomName, String playerName, int difficulty) {
        System.out.println("Creando el Room");
        this.room_dificulty = difficulty;
        sendInput("CREATE:" + roomName + ":" + playerName + ":" + difficulty);
    }

    public void joinRoom(String roomName, String playerName , int difficulty) {
        System.out.println("joinRoom" + playerName);
        sendInput("JOIN:" + roomName + ":" + playerName + ":" + difficulty);
    }

    public void startGame(String roomName) {
        System.out.println("StartGame" + roomName);
        sendInput("START:" + roomName);
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Conexión y recursos cerrados correctamente.");
        } catch (IOException e) {
            System.err.println("Error al cerrar los recursos: " + e.getMessage());
        }
    }
    

    
    
    private class Listener implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    processGameState(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processGameState(String gameState) {
            String[] parts = gameState.split(":");
            System.out.println("Ingresó en processGameState" + parts[0]);

            switch (parts[0]) {
               case "Close":
                System.out.println("LLego a close");
                break;
                case "USER_LIST":
                    System.out.println("USER_LIST Client Class");
                    List<String> users = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        users.add(parts[i]);
                    }
                    if (roomPanel != null) {
                        roomPanel.updateUsers(users);
                    }
                    System.out.println("Enviando Input");
                    
                    sendInput("USUARIOS_EN_ROOMPANEL: " +users.toString());
                    
                    break;
                case "COLISION":
                    System.out.println("Ingresó en markPlayerAsDead" + gameState);
                    if (gp != null ){
                                            gp.markPlayerAsDead(parts[1]);

                    }
                    break;
                case "ROOM_JOINED":
                    
                    break;
                case "USER_JOINED":
                    System.err.println("gameState + " + gameState);
                    if (roomPanel != null) {
                        roomPanel.displayMessage(gameState);
                    }
                    try {
                        int diff = Integer.parseInt(parts[2]);
                         setRoom_dificulty(diff);
                    } catch (NumberFormatException e) {
                        return;
                    }
                                
                                
                    break;
                case "ROOM_EXISTS":
                    break;
                case "MESSAGE":
                    System.out.println("MESSAGE Client Class");
                    if (roomPanel != null) {
                        roomPanel.displayMessage(gameState);
                    }
                break;
                case "RoomDificulty":
                break;
                case "Este es un mensaje para todos":
                processFoodCounts(gameState);
                break;
                case "WINNER":
                processWinner(gameState);    
                break;
                case "Salas Disponibles":
                 processRoomSettings(gameState);
                case "GAME_STARTED":
                    System.out.println("GAME_STARTED Client Class");
                    if (roomPanel != null) {
                        roomPanel.gameStarted();
                    }
                    startGamePanel();
                    break;
                case "SNAKE_POSITIONS":
                processSnakesPositions(gameState);
                 /*  System.out.println("SNAKE_POSITIONS Client Class");
                    if (roomPanel != null) {
                        roomPanel.updateSnakePositions(parts);
                    }*/
                    break;
                default:
                    break;
            }
        }
        private void processWinner(String message) {
            String[] parts = message.split(":");
            if (parts.length > 1) {
                System.out.println("Winner and rankings: " + message);

                String winner = parts[1];
                StringBuilder rankings = new StringBuilder("Ganador: " + winner);


                if (gp != null) {
                    gp.displayWinner(message.toString());
                } else {
                    System.out.println("GamePanel is null, cannot display winner and rankings");
                }
            } else {
                System.out.println("Invalid WINNER message format");
            }
        }


        private  void processFoodCounts(String message) {
               try {
                   System.out.println("Process Food Counts");
                   // Extracting the substring between curly braces
                   String data = message.substring(message.indexOf("{") + 1, message.lastIndexOf("}"));
                   Map<String, Integer> foodCounts = new HashMap<>();
                   System.out.println("foodCounts:" + foodCounts);

                   // Splitting each key-value pair
                   for (String entry : data.split(", ")) {
                       String[] keyValue = entry.split("=");
                       foodCounts.put(keyValue[0], Integer.parseInt(keyValue[1]));
                   }
                   System.out.println("foodCounts:" + foodCounts);

                   if (gp != null) {
                       // Updating the GamePanel with the new food counts
                       gp.updateOtherPlayerFruitCounts(foodCounts);
                   }else{
                       System.out.println("gp:" + " es nulo");
                   }
               } catch (Exception e) {
                   System.out.println("Error processing food counts: " + e.getMessage());
               }
           }
        
        
            private void processSnakesPositions(String message) {
            System.out.println("Mensaje recibido para procesar posiciones: " + message);


        }

        private void processBracketFormat(String message) {
            String content = message.substring("SNAKE_POSITIONS:".length());
            String[] parts = content.split("\\{");
            String snakeName = parts[0];
            String positions = parts[1].substring(0, parts[1].length() - 2); // Eliminar el último }, y la coma

            List<Point> points = parsePositions(positions);

            // Crear y configurar la serpiente con los puntos obtenidos
            Snake newSnake = new Snake();
            newSnake.setBody(points);
                    if (gp != null ){
                    System.out.println("Añadida serpiente");
                     gp.addSnake(snakeName, newSnake);

                    }else{
                     System.out.println("gp null");

                    }
                    // Si tienes un mapa para almacenar las serpientes por su nombre
                    // snakesMap.put(snakeName, newSnake);  // Suponiendo que tienes un mapa `snakesMap`

                    System.out.println("Posiciones de la serpiente " + snakeName + ": " + points);
            // Si tienes un mapa para almacenar las serpientes por su nombre
            // snakesMap.put(snakeName, newSnake);  // Suponiendo que tienes un mapa `snakesMap`

            System.out.println("Posiciones de la serpiente " + snakeName + ": " + points);
        }

        private void processColonFormat(String message) {
            String content = message.substring("SNAKE_POSITIONS:".length());
            String[] snakeSections = content.split(";");
            System.out.println("processColonFormat");

            for (String section : snakeSections) {
                if (!section.isEmpty()) {
                    int colonIndex = section.indexOf(':');
                    String snakeName = section.substring(0, colonIndex);
                    String positions = section.substring(colonIndex + 1, section.length() - 1); // Eliminar la última coma

                    List<Point> points = parsePositions(positions);

                    // Crear y configurar la serpiente con los puntos obtenidos
                    Snake newSnake = new Snake();
                    newSnake.setBody(points);
                    if (gp != null ){
                    System.out.println("Añadida serpiente");
                                        gp.addSnake(snakeName, newSnake);

                    }else{
                     System.out.println("gp null");

                    }
                    // Si tienes un mapa para almacenar las serpientes por su nombre
                    // snakesMap.put(snakeName, newSnake);  // Suponiendo que tienes un mapa `snakesMap`

                    System.out.println("Posiciones de la serpiente " + snakeName + ": " + points);
                }
            }
                        System.out.println("sALIO processColonFormat");
        }

        private List<Point> parsePositions(String positions) {
            List<Point> points = new ArrayList<>();
            String[] coords = positions.split(",");
            for (String coord : coords) {
                if (!coord.isEmpty()) {
                    String[] xy = coord.split("-");
                    int x = Integer.parseInt(xy[0]);
                    int y = Integer.parseInt(xy[1]);
                    points.add(new Point(x, y));
                }
            }
            return points;
        }


        
        /*
            private void processSnakesPositions(String message) {
                System.out.println("Mensaje recibido para procesar posiciones: " + message);

                // Remover el prefijo del mensaje
                
                
                /*
                String cleanedMessage = message.substring("SNAKE_POSITIONS:".length());
                System.out.println("Mensaje limpio: " + cleanedMessage);

                // Extraer el nombre del jugador y las posiciones usando una expresión regular
                Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\\{([^}]+)\\}");
                Matcher matcher = pattern.matcher(cleanedMessage);
                List<Point> snakeBody = new ArrayList<>();


                    // El nombre del jugador
                    String playerName = matcher.group(1);
                    System.out.println("Jugador: " + playerName);

                    // Las posiciones como una cadena de texto
                    String positions = matcher.group(2);

                    System.out.println("Posiciones: " + positions);
                    
                    if (gp != null) {  // Verificar si gp no es nulo antes de usarlo
                        System.out.println("Actualizando posición");
                        Snake snake = new Snake();
                        snake.setPositionFromString(positions);
                        gp.addSnake(playerName, snake);   
                    } else {
                        System.out.println("GamePanel no está inicializado (gp es nulo).");
                    }
                    
                    
            
            }
    */


        private void startGamePanel() {
            System.out.println("Ingresó en startGamePanel de Client class");
            /*
            SwingUtilities.invokeLater(() -> {
                JFrame frame = roomPanel.getParentFrame();
                frame.getContentPane().removeAll();

                GamePanel gamePanel = new GamePanel( getRoomPanel().getName() , frame,  Client.this.getRoom_dificulty(), true, Client.this);
                Client.this.gp = gamePanel;
                frame.add(gamePanel);
                frame.revalidate();
                gamePanel.requestFocusInWindow();
                
                gamePanel.startGame();
                
            });
            */
        }

        private void processRoomSettings(String gameState) {
            System.out.println("processRoomSettings" + gameState);
        }
    }

  
}
