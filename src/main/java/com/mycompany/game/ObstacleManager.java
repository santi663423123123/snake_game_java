package com.mycompany.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObstacleManager {
    private CopyOnWriteArrayList<Point> obstacles;
    private final int SIZE = 40;
    private Random random;
    private Image wallImage;

    public ObstacleManager() {
        obstacles = new CopyOnWriteArrayList<>();
        random = new Random();
        wallImage = SpriteLoader.loadSprite("/wall.png");
    }

    public void addObstacle(int difficulty) {
        int numObstacles;
        switch (difficulty) {
            case 1: numObstacles = 1; break; // Fácil
            case 2: numObstacles = random.nextInt(3) + 1; break; // Medio
            case 3: numObstacles = random.nextInt(4) + 2; break; // Difícil
            default: numObstacles = 1; break;
        }

        for (int i = 0; i < numObstacles; i++) {
            Point newObstacle;
            boolean validPosition;
            do {
                validPosition = true;
                newObstacle = new Point(random.nextInt(800 / SIZE) * SIZE, random.nextInt(600 / SIZE) * SIZE);

                // Verificar si el obstáculo se solapa con otros obstáculos
                for (Point obstacle : obstacles) {
                    if (newObstacle.equals(obstacle)) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);
            obstacles.add(newObstacle);
        }
    }

    public void draw(Graphics g) {
        for (Point obstacle : obstacles) {
            g.drawImage(wallImage, obstacle.x, obstacle.y, SIZE, SIZE, null);
        }
    }

    public boolean checkCollision(Snake snake) {
        Point head = snake.getHead();
        for (Point obstacle : obstacles) {
            if (head.equals(obstacle)) {
                return true;
            }
        }
        return false;
    }

    public List<Point> getObstacles() {
        return obstacles;
    }
}