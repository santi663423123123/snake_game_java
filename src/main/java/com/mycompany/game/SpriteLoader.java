package com.mycompany.game;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpriteLoader {
    public static Image loadSprite(String path) {
        try {
            if (SpriteLoader.class.getResource(path) == null) {
                System.err.println("Error: Could not find file " + path);
                return null;
            }
            return ImageIO.read(SpriteLoader.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
