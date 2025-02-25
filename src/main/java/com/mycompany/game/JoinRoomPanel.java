package com.mycompany.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class JoinRoomPanel extends JPanel {
    private JFrame parentFrame;
    private Client client;

    public JoinRoomPanel(JFrame parentFrame) {
      this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Set the background image
        setLayout(new BorderLayout());

        // Cargar y configurar la imagen de fondo
        JLabel background = new JLabel(new ImageIcon(loadIcon("/background.png").getImage().getScaledInstance(-1, -1, Image.SCALE_SMOOTH)));
        background.setLayout(new BorderLayout());
        add(background);

        // Configurar título

        // Add the title label
        JLabel titleLabel = new JLabel("Join Room", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.red);
        background.add(titleLabel, BorderLayout.NORTH);

        // Create the form panel with vertical layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS)); // Vertical layout
        formPanel.setOpaque(false);

        JLabel ipLabel = new JLabel("Server IP:", JLabel.CENTER);
        ipLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ipLabel.setForeground(Color.red);
        JTextField ipField = new JTextField("localhost");
        ipField.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel portLabel = new JLabel("Port:", JLabel.CENTER);
        portLabel.setFont(new Font("Arial", Font.BOLD, 24));
        portLabel.setForeground(Color.red);
        JTextField portField = new JTextField("12345");
        portField.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel roomNameLabel = new JLabel("Room Name:", JLabel.CENTER);
        roomNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        roomNameLabel.setForeground(Color.red);
        JTextField roomNameField = new JTextField();
        roomNameField.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel playerNameLabel = new JLabel("Player Name:", JLabel.CENTER);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        playerNameLabel.setForeground(Color.red);
        JTextField playerNameField = new JTextField();
        playerNameField.setFont(new Font("Arial", Font.BOLD, 24));

        JButton joinButton = new JButton("Join");
        joinButton.setFont(new Font("Arial", Font.BOLD, 24));

        // Align components vertically
        formPanel.add(ipLabel);
        formPanel.add(ipField);
        formPanel.add(portLabel);
        formPanel.add(portField);
        formPanel.add(roomNameLabel);
        formPanel.add(roomNameField);
        formPanel.add(playerNameLabel);
        formPanel.add(playerNameField);
        formPanel.add(joinButton);
        
        JButton backButton =  new JButton("back");
        formPanel.add(new JLabel());
        formPanel.add(backButton);
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí se elimina todo el contenido actual del parentFrame
                parentFrame.getContentPane().removeAll();

                // Aquí se crea y se añade el panel anterior al frame
                MultiplayerScreen mainMenuPanel = new MultiplayerScreen(parentFrame);
                parentFrame.add(mainMenuPanel);
                parentFrame.revalidate();  // Refresca y valida el contenido del frame
                parentFrame.repaint();  // Repinta el frame para asegurar que la UI se actualiza correctamente
            }
        });
        
        
       // Create a panel for the back button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                MultiplayerScreen mainMenuPanel = new MultiplayerScreen(parentFrame);
                parentFrame.add(mainMenuPanel);
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        buttonPanel.add(backButton);

        // Add the button panel to the bottom of the background panel
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Add the form panel to a transparent container and then to the background
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.add(formPanel);
        background.add(container, BorderLayout.CENTER);

        joinButton.setFont(new Font("Arial", Font.BOLD, 24));
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());
                String roomName = roomNameField.getText();
                String playerName = playerNameField.getText();

                // Update the client connection based on IP and port
                client = new Client(ip, port, playerName );
                Server.addClient(playerName, client);
                System.out.println("Añadido cliente con  " + playerName);
                
                
                client.joinRoom(roomName, playerName , 1);
                
                // Show RoomPanel
                SwingUtilities.invokeLater(() -> {
                    parentFrame.getContentPane().removeAll();
        
                    RoomPanel roomPanel = new RoomPanel(parentFrame, client, roomName, playerName );
                    client.setRoomPanel(roomPanel);
                    parentFrame.add(roomPanel);
                    parentFrame.revalidate();
                    roomPanel.requestFocusInWindow();
                });
            }
        });

        formPanel.add(ipLabel);
        formPanel.add(ipField);
        formPanel.add(portLabel);
        formPanel.add(portField);
        formPanel.add(roomNameLabel);
        formPanel.add(roomNameField);
        formPanel.add(playerNameLabel);
        formPanel.add(playerNameField);
        formPanel.add(new JLabel());  // Placeholder if needed
        formPanel.add(joinButton);

        // Add the form panel to a transparent container and then to the background
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
}
