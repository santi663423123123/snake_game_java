package com.mycompany.game;

import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game {
    private static boolean isFullScreen = true;
    private static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public static void main(String[] args) {
        JFrame frame = new JFrame("La viborita");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configurar pantalla completa
        //toggleFullScreen(frame);


        StartScreen startScreen = new StartScreen(frame);
        frame.add(startScreen);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    private static void toggleFullScreen(JFrame frame) {
        if (isFullScreen) {
            frame.dispose();
            frame.setUndecorated(true);
            gd.setFullScreenWindow(frame);
            frame.setVisible(true);
        } else {
            gd.setFullScreenWindow(null);
            frame.dispose();
            frame.setUndecorated(false);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }
}