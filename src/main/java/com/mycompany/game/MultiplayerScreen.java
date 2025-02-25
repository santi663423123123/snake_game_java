package com.mycompany.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiplayerScreen extends JPanel {
    private JFrame parentFrame;

    public MultiplayerScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Set the background image
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/fruit.png")));
        add(background);
        background.setLayout(new BorderLayout());

        // Add the title label
        JLabel titleLabel = new JLabel("Multiplayer", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        background.add(titleLabel, BorderLayout.NORTH);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false);

        JButton createRoomButton = createStyledButton("Create Room", "/createroompanel.jpg");
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRoom();
            }
        });

        JButton joinRoomButton = createStyledButton("Join Room", "/joinroom.jpg");
        joinRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinRoom();
            }
        });

        buttonPanel.add(createRoomButton);
        buttonPanel.add(joinRoomButton);

        // Add the button panel to a transparent container and then to the background
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.add(buttonPanel);

        background.add(container, BorderLayout.CENTER);
        
        
        
        
                JButton backButton =  new JButton("back");
        buttonPanel.add(new JLabel());
        buttonPanel.add(backButton);
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí se elimina todo el contenido actual del parentFrame
                parentFrame.getContentPane().removeAll();

                // Aquí se crea y se añade el panel anterior al frame
                StartScreen mainMenuPanel = new StartScreen(parentFrame);
                parentFrame.add(mainMenuPanel);
                parentFrame.revalidate();  // Refresca y valida el contenido del frame
                parentFrame.repaint();  // Repinta el frame para asegurar que la UI se actualiza correctamente
            }
        });
        
        
       // Create a panel for the back button at the bottom
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
        
        
        
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setIcon(new ImageIcon(getClass().getResource(iconPath))); // Set the icon
        button.setHorizontalTextPosition(SwingConstants.CENTER); // Center the text
        button.setVerticalTextPosition(SwingConstants.BOTTOM); // Position the text below the icon
        return button;
    }

    private void createRoom() {
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            CreateRoomPanel createRoomPanel = new CreateRoomPanel(parentFrame);
            parentFrame.add(createRoomPanel);
            parentFrame.revalidate();
            createRoomPanel.requestFocusInWindow();
        });
    }

    private void joinRoom() {
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            JoinRoomPanel joinRoomPanel = new JoinRoomPanel(parentFrame);
            parentFrame.add(joinRoomPanel);
            parentFrame.revalidate();
            joinRoomPanel.requestFocusInWindow();
        });
    }
}