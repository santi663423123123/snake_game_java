package com.mycompany.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.LinkedList;
import java.awt.event.KeyEvent;
import java.util.List;

public class Snake {
    private LinkedList<Point> body;
    private int direction;
    private int newDirection;
    private final int SIZE = 40;
    private Image snakeHead;
    private Image snakeBody;
    private Image invulnerableHead;
    private Image invulnerableBody;
    private boolean invulnerable;
    private long invulnerableEndTime;
    private boolean isBlinking;
    private final long BLINK_DURATION = 500; // Tiempo de parpadeo en milisegundos

    public Snake() {
        body = new LinkedList<>();
        body.add(new Point(40, 40));
        direction = KeyEvent.VK_RIGHT;
        newDirection = direction;
        snakeHead = SpriteLoader.loadSprite("/head.png");
        snakeBody = SpriteLoader.loadSprite("/body.png");
        invulnerableHead = SpriteLoader.loadSprite("/invulnerable_head.png");
        invulnerableBody = SpriteLoader.loadSprite("/invulnerable_body.png");
        invulnerable = false;
        invulnerableEndTime = 0;
        isBlinking = false;
    }

    public void changeDirection(int newDirection) {
        if (Math.abs(direction - newDirection) != 2) {
            this.newDirection = newDirection;
        }
    }

    public void move() {
        direction = newDirection; // Aplicar el nuevo cambio de dirección antes de mover
        Point head = body.getFirst();
        Point newPoint = new Point(head);

        switch (direction) {
            case KeyEvent.VK_UP: newPoint.y -= SIZE; break;
            case KeyEvent.VK_DOWN: newPoint.y += SIZE; break;
            case KeyEvent.VK_LEFT: newPoint.x -= SIZE; break;
            case KeyEvent.VK_RIGHT: newPoint.x += SIZE; break;
        }

        body.addFirst(newPoint);
        body.removeLast();
    }

    public void move(String positions) {
        body.clear();
        String[] coords = positions.split(",");
        for (String coord : coords) {
            String[] xy = coord.split("-");
            body.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
    }

    public void grow() {
        Point tail = body.getLast();
        Point newPoint = new Point(tail);

        switch (direction) {
            case KeyEvent.VK_UP: newPoint.y += SIZE; break;
            case KeyEvent.VK_DOWN: newPoint.y -= SIZE; break;
            case KeyEvent.VK_LEFT: newPoint.x += SIZE; break;
            case KeyEvent.VK_RIGHT: newPoint.x -= SIZE; break;
        }

        body.addLast(newPoint);  // Agrega el nuevo segmento al final
    }

    public void draw(Graphics g) {
        Image currentHead = snakeHead;
        Image currentBody = snakeBody;

        if (invulnerable) {
            if (isBlinking && System.currentTimeMillis() % BLINK_DURATION < BLINK_DURATION / 2) {
                currentHead = snakeHead; // Usar la imagen normal durante el parpadeo
                currentBody = snakeBody; // Usar la imagen normal durante el parpadeo
            } else {
                currentHead = invulnerableHead; // Usar la imagen de invulnerabilidad
                currentBody = invulnerableBody; // Usar la imagen de invulnerabilidad
            }
        }

        g.drawImage(currentHead, body.getFirst().x, body.getFirst().y, SIZE, SIZE, null);
        for (int i = 1; i < body.size(); i++) {
            g.drawImage(currentBody, body.get(i).x, body.get(i).y, SIZE, SIZE, null);
        }
    }
    public void setHead(Point newHead) {
        if (!body.isEmpty()) {
            body.set(0, newHead); // Reemplaza la posición actual de la cabeza
        } else {
            body.add(newHead); // Si la lista está vacía, añade el nuevo punto como cabeza
        }
    }
   public void setBody(List<Point> newBody) {
        body = new LinkedList<>(newBody); // Crea una nueva lista a partir de la proporcionada
    }
    
    
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
        if (invulnerable) {
            invulnerableEndTime = System.currentTimeMillis() + 2000; // 2 segundos de invulnerabilidad
            isBlinking = false;
        }
    }
    public void setPositionFromString(String posData) {
        String[] pos = posData.split(",");
        LinkedList<Point> newBody = new LinkedList<>();
        for (String p : pos) {
            String[] xy = p.split("-");
            newBody.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        this.body = newBody;
    }

    public void updateInvulnerability() {
        if (invulnerable) {
            if (System.currentTimeMillis() > invulnerableEndTime) {
                invulnerable = false;
                isBlinking = false;
            } else if (System.currentTimeMillis() > invulnerableEndTime - 1000) { // Empieza a parpadear en el último segundo
                isBlinking = true;
            }
        }
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerableEndTime(long time) {
        this.invulnerableEndTime = time;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public List<Point> getBody() {
        return body;
    }

    public String getPosition() {
        StringBuilder sb = new StringBuilder();
        for (Point point : body) {
            sb.append(point.x).append("-").append(point.y).append(",");
        }
        return sb.toString();
    }

   public boolean checkCollision(int width, int height) {
        Point head = getHead();
        // Verifica colisiones con los bordes del panel
        if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
            System.out.println("Collision with wall detected at: " + head);
            return true;
        }
        return false;
    }

    public boolean checkSelfCollision() {
        Point head = getHead();
        for (Point point : body) {
            if (head.equals(point) && head != point) {
                System.out.println("Self-collision detected at: " + head);
                return true;
            }
        }
        return false;
    }


}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Usuario
 */
