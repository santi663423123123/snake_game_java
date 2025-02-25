package com.mycompany.game;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private String roomname;

    public  String getRoomname() {
        return roomname;
    }
    private Map<String, Snake> snakes; // Mapear nombres de jugadores a sus serpientes

    private Food food;

    public Food getFood() {
        return food;
    }
    private ObstacleManager obstacleManager;
    private GameThread gameThread;
    private JFrame parentFrame;
    private int fruitCount;
    private boolean showScoreboard = true;
    private boolean isMultiplayer ;
    private Map<String, Boolean> playerDeaths = new ConcurrentHashMap<>();
    
    public boolean isIsMultiplayer() {
        return isMultiplayer;
    }
    public void markPlayerAsDead(String playerName) {
        playerDeaths.put(playerName, true);
        repaint();
    }
    public void setIsMultiplayer(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
    }
    private Map<String, Integer> otherPlayerFruitCounts;
    private int difficulty;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    private Image background;
    private ConcurrentLinkedQueue<Integer> keyQueue;
    Client client; // Añadir cliente

    private Timer gameTimer;
    private int secondsPlayed;

    public int getSecondsPlayed() {
        return secondsPlayed;
    }

    public GamePanel(String roomname, JFrame parentFrame, int difficulty, boolean isMultiplayer, Client client) {
        this.roomname = roomname;
        this.parentFrame = parentFrame;
        this.difficulty = difficulty;
        this.isMultiplayer = isMultiplayer;
        this.client = client;
        this.keyQueue = new ConcurrentLinkedQueue<>();
        this.snakes = new ConcurrentHashMap<>();
        setPreferredSize(new Dimension(800, 600));
        setBackground(null);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyQueue.offer(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_S) { // Supongamos que 'S' alterna el scoreboard
                    SwingUtilities.invokeLater(() -> {
                        showScoreboard = !showScoreboard;
                        repaint();
                    });
                }
            }
        });

        // Iniciar con una serpiente
        Snake snake = new Snake();
        if (client != null && client.getPlayerName() != null) {
            snakes.put(client.getPlayerName(), snake);
        } else {
            snakes.put("singleplayer", snake);
        }
        obstacleManager = new ObstacleManager();
        food = new Food(snake, obstacleManager);

        gameThread = new GameThread(this, snakes, food, obstacleManager, difficulty, keyQueue, client);
        fruitCount = 0;
        background = SpriteLoader.loadSprite("/background_game.jpg");

        // Inicializar el contador de tiempo
        secondsPlayed = 0;
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsPlayed++;
                repaint();
            }
        });
    }
    
    public void startGame() {
        System.out.println("La dificultad de GamePanel es: " + getDifficulty());
        gameThread.start();
        gameTimer.start(); // Iniciar el temporizador cuando comienza el juego
    }
    public void endGameMultiplePlayer() {
        gameThread.stopGame();
        gameTimer.stop(); // Detener el temporizador cuando termina el juego
        SwingUtilities.invokeLater(() -> {
            showMultiplayerGameOverPanel();
        });
    }
    public void endGame() {
        gameThread.stopGame();
        gameTimer.stop(); // Detener el temporizador cuando termina el juego
        SwingUtilities.invokeLater(() -> {
            showGameOverPanel();
        });
    }

    public int getFruitCount() {
        return fruitCount;
    }
    public Map<String, Snake> getSnakes() {
        return snakes;
    }

    public void setSnakes(Map<String, Snake> snakes) {
        this.snakes = snakes;
    }
    public void addSnake(String playerName, Snake snake) {
        System.out.println("Ingresó en add snake");
        snakes.put(playerName, snake);
    }
private void showGameOverPanel() {  
    removeAll();
    setLayout(new BorderLayout());
    setBackground(Color.WHITE); // Establece el color de fondo del panel principal
    setOpaque(true); // Asegura que el color de fondo se muestre

    JLabel messageLabel = new JLabel("<html>Game Over! The snake hit the wall.<br>Fruits eaten: " + fruitCount + "<br>Time played: " + getFormattedTime() + "</html>", JLabel.CENTER);
    messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
    messageLabel.setBackground(Color.WHITE); // Fondo blanco para la etiqueta
    messageLabel.setOpaque(true); // Permite que se vea el color de fondo de la etiqueta
    add(messageLabel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE); // Fondo blanco para el panel de botones
    buttonPanel.setOpaque(true);

    JButton restartButton = new JButton("Restart");
    JButton mainMenuButton = new JButton("Main Menu");

    restartButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            restartGame();
        }
    });

    mainMenuButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            returnToMainMenu();
        }
    });

    buttonPanel.add(restartButton);
    buttonPanel.add(mainMenuButton);
    add(buttonPanel, BorderLayout.SOUTH);

    revalidate();
    repaint();
    System.out.println("Game Over");
}

    private void restartGame() {
        snakes.clear();
        Snake snake = new Snake();
        if (client != null ) {
        snakes.put(client.getPlayerName(), snake); // Cambiar esto a un identificador único para cada jugador
        }else{
        snakes.put("singleplayer", snake); // Cambiar esto a un identificador único para cada jugador
        }
        obstacleManager = new ObstacleManager();
        food = new Food(snake, obstacleManager);
        gameThread = new GameThread(this, snakes, food, obstacleManager, difficulty, keyQueue, client);
        fruitCount = 0;
        secondsPlayed = 0; // Reiniciar el contador de tiempo
        gameTimer.restart(); // Reiniciar el temporizador
        removeAll();
        setLayout(null);  // Reset layout
        startGame();
        revalidate();
        repaint();
    }

    private void returnToMainMenu() {
        parentFrame.getContentPane().removeAll();
        StartScreen startScreen = new StartScreen(parentFrame);
        parentFrame.add(startScreen);
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    public void incrementFruitCount() {
        fruitCount++;
    }
    
    
    


    private void showMultiplayerGameOverPanel() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Establece el color de fondo del panel principal
        setOpaque(true);  // Asegura que el color de fondo se muestre

        JLabel messageLabel = new JLabel("<html>Game Over! Waiting for other players to finish.<br>Fruits eaten: " + fruitCount + "<br>Time played: " + getFormattedTime() + "</html>", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setBackground(Color.WHITE);  // Fondo blanco para la etiqueta
        messageLabel.setOpaque(true);  // Permite que se vea el color de fondo de la etiqueta
        add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);  // Fondo blanco para el panel de botones
        buttonPanel.setOpaque(true);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
        System.out.println("Waiting for other players to finish.");
    }



public void displayWinner(String winnerInfo) {
    // Crear un JTextArea para mostrar el texto
    JTextArea textArea = new JTextArea(winnerInfo);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    // Colocar el JTextArea dentro de un JScrollPane
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(300, 200));

    // Definir las opciones para los botones
    String[] options = {"Aceptar", "Ver Detalles", "Cerrar"};
    
    // Mostrar el JOptionPane con las opciones
    int response = JOptionPane.showOptionDialog(
        this,
        scrollPane,
        "Resultados del juego",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        options,
        options[0]
    );

    // Manejar las respuestas
    switch (response) {
        case 0: // Aceptar
            System.out.println("Aceptar fue seleccionado.");
            performAcceptAction();
            break;
        case 1: // Ver Detalles
            System.out.println("Ver Detalles fue seleccionado.");
            showGameDetails();
            break;
        case 2: // Cerrar
            System.out.println("Cerrar fue seleccionado.");
            closeGamePanel();
            break;
        default:
            System.out.println("Ninguna opción fue seleccionada.");
            break;
    }
}

private void performAcceptAction() {
    // Define la lógica que quieres ejecutar cuando se presione "Aceptar"
    client.sendInput("CLOSE:SV:CLOSE:CONNECTION");
    System.out.println("Ejecutando acción de aceptar...");
     returnToMainMenu();
}

private void showGameDetails() {
    StringBuilder details = new StringBuilder();
    details.append("Detalles del juego:\n\n");
    details.append("Jugador: ").append(client.getPlayerName()).append("\n");
    details.append("Frutas comidas: ").append(fruitCount).append("\n");
    details.append("Tiempo jugado: ").append(getFormattedTime()).append("\n");
    details.append("Dificultad: ").append(difficulty).append("\n\n");

    if (otherPlayerFruitCounts != null && !otherPlayerFruitCounts.isEmpty()) {
        details.append("Frutas comidas por otros jugadores:\n");
        for (Map.Entry<String, Integer> entry : otherPlayerFruitCounts.entrySet()) {
            details.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
    }

    // Crear un JTextArea para mostrar los detalles
    JTextArea textArea = new JTextArea(details.toString());
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);

    // Colocar el JTextArea dentro de un JScrollPane
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    // Mostrar el JOptionPane con los detalles
    JOptionPane.showMessageDialog(this, scrollPane, "Detalles del juego", JOptionPane.INFORMATION_MESSAGE);
    
    System.out.println("Mostrando detalles del juego...");
}
private void closeGamePanel() {
    // Aquí puedes definir la lógica para cerrar el panel del juego
    System.out.println("Cerrando el panel del juego...");
}
    private String getFormattedTime() {
        int minutes = secondsPlayed / 60;
        int seconds = secondsPlayed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        food.draw(g);
        obstacleManager.draw(g);

        // Dibuja las serpientes
        for (Snake snake : snakes.values()) {
            snake.draw(g);
        }

        // Dibujar el scoreboard solo si está visible
        if (showScoreboard) {
            // Configuración de la fuente y colores
            g.setColor(new Color(0, 0, 0, 150)); // Fondo semi-transparente
            g.fillRoundRect(10, 10, 200, 100, 15, 15); // Fondo del scoreboard

            g.setColor(Color.WHITE); // Color del texto
            g.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente personalizada

            int yPos = 30; // Posición inicial en y

            // Dibujar el puntaje del jugador local
            g.drawString("dificultad " + getDifficulty(), 20, yPos);
            yPos += 20;
            if ( this.client != null ){
                        g.drawString("Cliente " + this.client.getPlayerName() , 20, yPos);
    
            }
            yPos += 20;
            // Mostrar el tiempo jugado
            g.drawString("Time played: " + getFormattedTime(), 20, yPos);
            yPos += 20;

            // Dibujar el puntaje de otros jugadores
            if (otherPlayerFruitCounts != null) {
                for (Map.Entry<String, Integer> entry : otherPlayerFruitCounts.entrySet()) {
                    String playerName = entry.getKey();
                    g.drawString(playerName + " - Frutas: " + entry.getValue(), 20, yPos);

                    // Dibujar una cruz al lado del nombre del jugador si ha muerto
                    if (playerDeaths.getOrDefault(playerName, false)) {
                        g.setColor(Color.RED);
                        g.drawLine(5, yPos - 10, 15, yPos); // Línea diagonal \
                        g.drawLine(5, yPos, 15, yPos - 10); // Línea diagonal /
                        g.setColor(Color.WHITE); // Volver al color blanco
                    }

                    yPos += 20;
                }
            }
        }
    }

    // Método para actualizar el conteo de frutas de otros jugadores
    public void updateOtherPlayerFruitCounts(Map<String, Integer> newCounts) {
        otherPlayerFruitCounts = new ConcurrentHashMap<>(newCounts);
        repaint();
    }

    public void updateSnakes() {
        for (Snake snake : snakes.values()) {
            snake.move();  // Asumiendo que Snake tiene un método move
        }
    }
}
