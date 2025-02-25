
package com.mycompany.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class StartScreen extends JPanel {
    private JFrame parentFrame;

    public StartScreen(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Set the background image
        JLabel background = new JLabel(loadIcon("/principal.jpg"));
        add(background);
        background.setLayout(new BorderLayout());

        // Add the title label
        JLabel titleLabel = new JLabel("Snake Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        background.add(titleLabel, BorderLayout.NORTH);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);

        JButton singlePlayerButton = createStyledButton("Single Player", "/singleplayer.png");
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDifficultySelection(false);
            }
        });

        JButton multiPlayerButton = createStyledButton("Multiplayer", "/multiplayer.png");
        multiPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMultiplayerScreen();
            }
        });

        JButton exitButton = createStyledButton("Exit", "/exit.png");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(singlePlayerButton);
        buttonPanel.add(multiPlayerButton);
        buttonPanel.add(exitButton);

        // Add the button panel to a transparent container and then to the background
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.add(buttonPanel);

        background.add(container, BorderLayout.CENTER);
        
        JLabel authorLabel = new JLabel("Hecho por Santiago Arteta", JLabel.CENTER);
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        authorLabel.setForeground(Color.RED);
        background.add(authorLabel, BorderLayout.SOUTH);
        
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setIcon(loadIcon(iconPath)); // Set the icon
        button.setHorizontalTextPosition(SwingConstants.CENTER); // Center the text
        button.setVerticalTextPosition(SwingConstants.BOTTOM); // Position the text below the icon
        return button;
    }

    private void showDifficultySelection(boolean isMultiplayer) {
        removeAll();
        setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel("Select Difficulty Level", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(messageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton easyButton = createStyledButton("Easy", "/easy.png");
        JButton mediumButton = createStyledButton("Medium", "/medium.png");
        JButton hardButton = createStyledButton("Hard", "/hard.png");

        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(1, isMultiplayer);
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(2, isMultiplayer);
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(3, isMultiplayer);
            }
        });
        JButton backButton = createStyledButton("Back", "/back.png");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             parentFrame.getContentPane().removeAll();
            StartScreen start = new StartScreen(parentFrame);
            parentFrame.add(start);
            parentFrame.revalidate();
            start.requestFocusInWindow();
            }
        });

        add(buttonPanel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);
        add(buttonPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void showMultiplayerScreen() {
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            MultiplayerScreen multiplayerScreen = new MultiplayerScreen(parentFrame);
            parentFrame.add(multiplayerScreen);
            parentFrame.revalidate();
            multiplayerScreen.requestFocusInWindow();
        });
    }

    private void startGame(int difficulty, boolean isMultiplayer) {
        SwingUtilities.invokeLater(() -> {
            parentFrame.getContentPane().removeAll();
            Client client = null;
            if (isMultiplayer) {
                client = new Client("localhost", 12345 , client.getPlayerName());
            }
            GamePanel gamePanel = new GamePanel("singleplayer", parentFrame, difficulty, isMultiplayer, client);
            parentFrame.add(gamePanel);
            parentFrame.revalidate();
            gamePanel.requestFocusInWindow();  // Ensure the game panel has focus
            gamePanel.startGame();
        });
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


