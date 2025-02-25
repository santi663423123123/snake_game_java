package com.mycompany.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class CreateRoomPanel extends JPanel {
    private JFrame parentFrame;
    private Client client;

    public CreateRoomPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Set the background image
        JLabel background = new JLabel(loadIcon("/createroompanel.jpg"));
        add(background);
        background.setLayout(new BorderLayout());

        // Add the title label
        JLabel titleLabel = new JLabel("Create Room", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.GREEN);
        background.add(titleLabel, BorderLayout.NORTH);

        // Create the form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));  // Changed to 4 rows to accommodate difficulty selection
        formPanel.setOpaque(false);

        JLabel roomNameLabel = new JLabel("Room Name:", JLabel.RIGHT);
        roomNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        roomNameLabel.setForeground(Color.GREEN);
        JTextField roomNameField = new JTextField();
        roomNameField.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel playerNameLabel = new JLabel("Player Name:", JLabel.RIGHT);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        playerNameLabel.setForeground(Color.GREEN);
        JTextField playerNameField = new JTextField();
        playerNameField.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel difficultyLabel = new JLabel("Difficulty:", JLabel.RIGHT);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        difficultyLabel.setForeground(Color.GREEN);
        JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyBox.setFont(new Font("Arial", Font.BOLD, 24));

        JButton createButton = new JButton("Create");
        createButton.setFont(new Font("Arial", Font.BOLD, 24));
        
        
        JButton backbutton = new JButton("Back");
        backbutton.setFont(new Font("Arial", Font.BOLD, 24));
        backbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             parentFrame.getContentPane().removeAll();
            StartScreen start = new StartScreen(parentFrame);
            parentFrame.add(start);
            parentFrame.revalidate();
            start.requestFocusInWindow();
            }
        });
        
        
        
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = roomNameField.getText();
                String playerName = playerNameField.getText();
                String selectedDifficulty = (String) difficultyBox.getSelectedItem();

                int difficulty = mapDifficultyToInt(selectedDifficulty);

                // Iniciar el servidor local
               if (  Server.startServer() ){
                   System.out.println("OK");
               }else{
                    JOptionPane.showMessageDialog(parentFrame, "Error nombre de Room Duplicado , intenta con otro", "Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println("ERROR");

                    // Volver a la pantalla anterior después de mostrar el error
                    SwingUtilities.invokeLater(() -> {
                        parentFrame.getContentPane().removeAll();
                        // Aquí deberías cargar el panel anterior. Por ejemplo:
                        parentFrame.add(new MultiplayerScreen(parentFrame)); // Asegúrate de tener un MainMenuPanel o el panel anterior adecuado
                        parentFrame.revalidate();
                        parentFrame.repaint();
                    });

                    return; // Salir del ActionListener si hay un error
               }

                // Conectar el cliente al servidor local
                client = new Client("localhost", 12345, playerName);
                client.createRoom(roomName, playerName ,difficulty );
                Server.updateCustomMap(roomName,difficulty);

                Server.addClient(playerName, client);
                // Mostrar RoomPanel
                SwingUtilities.invokeLater(() -> {
                    parentFrame.getContentPane().removeAll();
                    System.out.println("difficulty:" + difficulty);
                    RoomPanel roomPanel = new RoomPanel(parentFrame, client, roomName, playerName );
                    client.setRoomPanel(roomPanel);
                    parentFrame.add(roomPanel);
                    parentFrame.revalidate();
                    roomPanel.requestFocusInWindow();
                });
            }
            
            
            
        });

        formPanel.add(roomNameLabel);
        formPanel.add(roomNameField);
        formPanel.add(playerNameLabel);
        formPanel.add(playerNameField);
        formPanel.add(difficultyLabel);
        formPanel.add(difficultyBox);
        formPanel.add(new JLabel());
        formPanel.add(createButton);
        formPanel.add(backbutton);
        // Add the form panel to a transparent container and then to the background
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.add(formPanel);

        background.add(container, BorderLayout.CENTER);
    }

    private ImageIcon loadIcon(String path) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    private int mapDifficultyToInt(String difficulty) {
    switch (difficulty) {
        case "Easy":
            return 1;
        case "Medium":
            return 2;
        case "Hard":
            return 3;
        default:
            throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
    }
}
    
}
