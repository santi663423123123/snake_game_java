package com.mycompany.game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel {
    private JFrame parentFrame;
    public Client client;
    private String roomName;
    private DefaultListModel<String> userListModel;
    String playerName; // Agregar campo para almacenar el nombre del jugador
    private JTextArea chatArea;
    private JTextField inputField;
    private JTextArea userListArea; // Cambiar a JTextArea para mostrar la lista de usuarios
    private JButton startButton;
    private int Difficulty;
    
    public RoomPanel(JFrame parentFrame, Client client, String roomName, String playerName) { // Modificar el constructor para aceptar playerName
        this.parentFrame = parentFrame;
        this.client = client;
        userListModel = new DefaultListModel<>();
        this.roomName = roomName;
        this.playerName = playerName; // Asignar el nombre del jugador
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Room: " + roomName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        add(titleLabel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        userListArea = new JTextArea(); // Cambiar a JTextArea para mostrar la lista de usuarios
        userListArea.setEditable(false);
        JScrollPane userScrollPane = new JScrollPane(userListArea);
        add(userScrollPane, BorderLayout.EAST);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 24));
        inputField.addActionListener(e -> sendMessage());
        add(inputField, BorderLayout.SOUTH);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.addActionListener(e -> client.startGame(roomName));
        add(startButton, BorderLayout.WEST);
        displayMessage("player join");
    }

    private void sendMessage() {
        System.out.println("Ingreso en sendMessage de RoomPanel");
        String message = inputField.getText();
        if (!message.isEmpty()) {
            client.sendInput("MESSAGE:" + roomName + ":" + message);
            inputField.setText("");
        }
    }

    public void displayMessage(String message) {
        System.out.println("Ingreso en displayMessage de RoomPanel");
        chatArea.append(message + "\n");
    }

    public void updateUsers(List<String> users) {
        userListArea.setText(""); // Limpiar el área de texto antes de actualizar
        for (String user : users) {
            userListArea.append(user + "\n"); // Añadir cada usuario en una nueva línea
        }
    }
    
    public void gameStarted() {
        startButton.setEnabled(false);
        displayMessage("Game Started!");
        System.out.println("Ingreso en gameStarted de RoomPanel");
        SwingUtilities.invokeLater(() -> {
            System.out.println("Invocando...");
            System.out.println("client.getRoom_dificulty()" + client.getRoom_dificulty());

            parentFrame.getContentPane().removeAll();
            GamePanel gamePanel = new GamePanel(roomName, parentFrame, client.getRoom_dificulty(), true, client);
            parentFrame.add(gamePanel);
            parentFrame.revalidate();
            gamePanel.requestFocusInWindow();
            gamePanel.startGame();
            client.setGamePanel(gamePanel);
        });
    }

    public void updateSnakePositions(String[] positions) {
        System.out.println("Ingresó en updateSnakePositions de RoomPanel");
        // Actualizar la interfaz con las posiciones de las serpientes
        // Esto se implementaría en el contexto del juego
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public int getDifficulty() {
        return Difficulty;
    }
}
