package com.mycompany.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Random;

public class Food {
    private Point position;
    private final int SIZE = 40;
    private final int SAFE_MARGIN = 2;
    private Image foodImage;
    private Snake snake;
    private ObstacleManager obstacleManager;

    public Food(Snake snake, ObstacleManager obstacleManager) {
        this.snake = snake;
        this.obstacleManager = obstacleManager;
        spawn();
        foodImage = SpriteLoader.loadSprite("/fruit.png");
    }

    public void spawn() {
        Random random = new Random();
        boolean validPosition = false;
        int maxX = 800 / SIZE;
        int maxY = 600 / SIZE;

        while (!validPosition) {
            int x = random.nextInt(maxX - 2 * SAFE_MARGIN) + SAFE_MARGIN;
            int y = random.nextInt(maxY - 2 * SAFE_MARGIN) + SAFE_MARGIN;

            position = new Point(x * SIZE, y * SIZE);
            validPosition = true;

            // Comprobar si la posición de la fruta coincide con alguna parte del cuerpo de la serpiente
            for (Point segment : snake.getBody()) {
                if (position.equals(segment)) {
                    validPosition = false;
                    break;
                }
            }

            // Comprobar si la posición de la fruta coincide con algún obstáculo
            for (Point obstacle : obstacleManager.getObstacles()) {
                if (position.equals(obstacle)) {
                    validPosition = false;
                    break;
                }
            }
        }
    }

    public void draw(Graphics g) {
        g.drawImage(foodImage, position.x, position.y, SIZE, SIZE, null);
    }

    public Point getPosition() {
        return position;
    }
}