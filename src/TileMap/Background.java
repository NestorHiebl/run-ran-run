package TileMap;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Background {
    private BufferedImage image;

    private double x;
    private double y;
    private double dx;
    private double dy;

    private double moveScale;

    public Background(String s, double moveScale) {
        try {
            image = ImageIO.read(
                    new File(s)
            );
            this.moveScale = moveScale;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPosition(double x, double y) {
        this.x *= moveScale;
        this.y *= moveScale;

    }

    public void setVector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx;
        y += dy;
        /* Ensure the image wraps around and that x and y are always greater than 0 */
        x = moduloDouble(x, GamePanel.WIDTH);
        y = moduloDouble(y, GamePanel.HEIGHT);
    }

    public void draw(Graphics2D g) {
        g.drawImage(image, (int) x, (int) y, null);

        /* Fill up empty space on border when scrolling horizontally */
        if (x > 0) {
            g.drawImage(image, (int) x - GamePanel.WIDTH, (int) y, null);
        }

        /* Fill up empty space on border when scrolling vertically */
        if (y > 0) {
            g.drawImage(image, (int) x, (int) y - GamePanel.HEIGHT, null);
        }

        /* Fill up diagonal empty space when scrolling both horizontally and vertically */
        if (x > 0 && y > 0) {
            g.drawImage(image, (int) x - GamePanel.WIDTH, (int) y - GamePanel.HEIGHT, null);
        }
    }

    private double moduloDouble(double x, double m) {
        if (m < 2) {
            throw new IllegalArgumentException("Modulo cannot be less than 2");
        }

        if (x >= 0) return x % m;
        /* If this is reached, x is negative */
        return m + (x % m);
    }
}
