package com.mycompany.game;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class GameThread extends Thread {
    private GamePanel gamePanel;
    private Map<String, Snake> snakes;
    private Food food;
    private ObstacleManager obstacleManager;
    private boolean running;
    private int delay;
    private int difficulty;
    private static ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();
    private static final int INVULNERABILITY_TIME = 2000; // 2 segundos de invulnerabilidad
    private Queue<Integer> keyQueue;
    Client client; // Para enviar movimientos al servidor

    public GameThread(GamePanel gamePanel, Map<String, Snake> snakes, Food food, ObstacleManager obstacleManager, int difficulty, Queue<Integer> keyQueue, Client client) {
        this.gamePanel = gamePanel;
        this.snakes = snakes;
        this.food = food;
        this.obstacleManager = obstacleManager;
        this.running = true;
        this.difficulty = difficulty;
        this.keyQueue = keyQueue;
        this.client = client; // Inicializar cliente
        // Ajustar la velocidad del juego según la dificultad
        switch (difficulty) {
            case 1: this.delay = 100; break; // Fácil
            case 2: this.delay = 75; break;  // Medio
            case 3: this.delay = 50; break;  // Difícil
            default: this.delay = 75; break;
        }
        startInvulnerability(); // Inicio de la invulnerabilidad al comienzo del juego
                    
    }

    @Override
    public void run() {
        while (running) {
            // Procesar entradas de teclado de inmediato
            processInput();
          //  sendSnakePositions();
     
            // Mover todas las serpientes
            for (Snake snake : snakes.values()) {
                snake.move();
                snake.updateInvulnerability(); // Actualizar el estado de invulnerabilidad
                      
                
                // Verificar colisiones después de mover la serpiente
                if (!snake.isInvulnerable() && (snake.checkCollision(gamePanel.getWidth(), gamePanel.getHeight()) || 
                    snake.checkSelfCollision() || 
                    obstacleManager.checkCollision(snake))) {
                    if (this.client != null ){
                       NotifyColision();  
                    }
                   
                    running = false;
                    if(gamePanel.isIsMultiplayer() == true ){
                        gamePanel.endGameMultiplePlayer();
                    }else{
                        gamePanel.endGame();  

                    }
                    
                    return;
        
                }

                if (snake.getHead().equals(food.getPosition())) {
                    snake.grow();
                    food.spawn();
                    gamePanel.incrementFruitCount();
                    obstacleManager.addObstacle(difficulty);
                    increaseSpeed();
                    startInvulnerability(); // Reiniciar la invulnerabilidad al comer una fruta
                }
            }

            // Enviar posición de la serpiente al servidor, si es multijugador
           if (client != null) {
                  updateScoreboard();
                 // sendSnakePositions();
           }
                      

            gamePanel.repaint();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processInput() {
        Integer keyCode;
        while ((keyCode = keyQueue.poll()) != null) {
            // Solo procesar teclas direccionales
            if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
                for (Snake snake : snakes.values()) {
                    snake.changeDirection(keyCode);
                }
                // Enviar dirección al servidor si es multijugador
                if (client != null) {
                    client.sendInput("MOVE:" + keyCode);
                }
            }
        }
    }
    void NotifyColision() {
            System.out.println("Ingreso en NotifyColision");
            String playerName = this.getName();

            String clientPlayerName = client.getRoomPanel().client.getPlayerName(); // Obtener nombre del jugador
           

            // Prepara el mensaje con la posición de la serpiente
            String colisionMessage = String.format("COLISION:%s:%s:%s:%d",
                                                   clientPlayerName,
                                                   gamePanel.getRoomname(),
                                                   gamePanel.getSecondsPlayed(),
                                                   gamePanel.getFruitCount()        
                                                   );
            System.out.println("Enviando: " + colisionMessage);
            client.sendInput(colisionMessage);

            
    };
    
    private void sendSnakePositions() {
        StringBuilder positionsMessage = new StringBuilder("SNAKE_POSITIONS:");
        for (Map.Entry<String, Snake> entry : snakes.entrySet()) {
            String snakeId = entry.getKey();
            Snake snake = entry.getValue();
            positionsMessage.append(snakeId).append(":").append(snake.getPosition());
            System.out.println("snakeId " + snakeId);
            System.out.println("snake " + snake );
            System.out.println(" snake.getPosition() " + snake.getPosition());
        }
        client.sendInput(positionsMessage.toString());
    }
    
    
    private  void updateScoreboard() {

            String playerName = this.getName();

            String clientPlayerName = client.getRoomPanel().client.getPlayerName(); // Obtener nombre del jugador
           



            // Prepara el mensaje con la posición de la serpiente
            String positionMessage = String.format("SCOREBOARD:%s:%s:%s:%s:%d",
                                                   playerName,
                                                   "test",
                                                   clientPlayerName,
                                                   client.getRoomPanel().getName(),
                                                   gamePanel.getFruitCount()        
                                                           );
            System.out.println("gamePanel.getFruitCount() " + gamePanel.getFruitCount());
            System.out.println("gamePanel.getFruitCount() " +  food.getPosition());
            // Enviar el mensaje al servidor
            System.out.println("El room es : " + client.getRoomPanel().getName());
            System.out.println("Mensaje:" + positionMessage);
            client.sendInput(positionMessage);

        
    }


    private void increaseSpeed() {
        if (delay > 20) {
            delay -= 2; // Aumenta la velocidad de la serpiente
        }
    }

    private void startInvulnerability() {
        for (Snake snake : snakes.values()) {
            snake.setInvulnerable(true);
            snake.setInvulnerableEndTime(System.currentTimeMillis() + INVULNERABILITY_TIME);
        }
    }

    public void stopGame() {
        running = false;
    }
}
