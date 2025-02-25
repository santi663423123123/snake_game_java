/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.game;

/**
 *
 * @author Usuario
 */
import java.util.concurrent.ConcurrentHashMap;

// Estructura para almacenar el estado del juego
class GameState {
    ConcurrentHashMap<String, Snake> snakes;

    public GameState() {
        this.snakes = new ConcurrentHashMap<>();
    }

    public synchronized void updateSnake(String player, Snake snake) {
        snakes.put(player, snake);
    }

    public ConcurrentHashMap<String, Snake> getSnakes() {
        return snakes;
    }
}