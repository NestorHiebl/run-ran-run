package TileMap;

import java.awt.image.BufferedImage;

public class Tile {
    private final BufferedImage image;
    private final Tile.type type;

    public enum type {
        PASSABLE,
        BLOCKED
    }

    public Tile(BufferedImage image, Tile.type type) {
        this.image = image;
        this.type = type;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Tile.type getType() {
        return this.type;
    }
}
