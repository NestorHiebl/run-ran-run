package TileMap;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class TileMap {

    /* Position */
    /**
     * The map's location is updated such that it is pushed backwards into space as the player moves to
     * the right. In other words, as the player moves, the x value becomes smaller and smaller.
     */
    private double x, y;

    /* Bounds */
    private int xmin, ymin, xmax, ymax;

    /* Map */
    private Vector<int[]> mapStructure;

    private final int tileSize;
    private int numRows, numCols;
    private int width, height;

    /***** Tileset *****/
    private BufferedImage tileset;
    /* Width of the tileset */
    private int numTilesAcross;
    /* Game height in tiles. Is 8 with a pixel height of 240 and tile size of 30 */
    private final int numTilesVertical;
    /* 2D array containing available tile properties */
    private Tile[][] tiles;

    // Drawing
    private int rowOffset, colOffset;
    private int numRowsToRender, numColsToRender;

    /* Map builder and concurrency resources */
    private MapBuilder builder;
    private Semaphore mapStructureAvailable;

    public TileMap(int tileSize) {
        this.tileSize = tileSize;
        numRowsToRender = GamePanel.HEIGHT / tileSize + 2;
        numColsToRender = GamePanel.WIDTH / tileSize + 2;
        numTilesVertical = GamePanel.HEIGHT / tileSize;

        this.mapStructure = new Vector<>();

        this.mapStructureAvailable = new Semaphore(1);

        this.builder = new MapBuilder(this, mapStructureAvailable);
        /* Run the builder a set number of times until there is enough initial map to go on */
        this.appendTileConfig(TileConfiguration.DEFAULT);
        builder.setWorkLoad(10);
        builder.run();

        numCols = mapStructure.size();
        numRows = numTilesVertical;

        width = numCols * tileSize;
        height = numRows * tileSize;

        xmin = GamePanel.WIDTH - width;
        xmax = 0;
        ymin = GamePanel.HEIGHT - height;
        ymax = 0;
    }

    public void loadTiles(String s) {
        try {
            tileset = ImageIO.read(new File(s));
            numTilesAcross = tileset.getWidth() / tileSize;
            // We assume that the image has two rows of tiles
            tiles = new Tile[2][numTilesAcross];
            // Create a subimage container to save the individual tile images in
            BufferedImage subimage;
            // Load in tiles, distinguishing between blocked and normal ones
            for (int col = 0; col < numTilesAcross; col++) {

                // Load and mark tiles in the first row as normal
                subimage = tileset.getSubimage(
                        /* Subimage position */
                        col * tileSize, 0,
                        /* Subimage dimensions */
                        tileSize, tileSize);
                tiles[0][col] = new Tile(subimage, Tile.PASSABLE);

                // Load and mark tiles in the second row as blocked
                subimage =tileset.getSubimage(
                        /* Subimage position */
                        col * tileSize, tileSize,
                        /* Subimage dimensions */
                        tileSize, tileSize);
                tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Get the tile type at a specific pair of coordinates. Mostly useful for collision checking.
     * Out-of-bounds indices will always return a passable value. This function reads from the protected MapStructure
     * Vector, and is called as part of the player update function. TODO: Add protection
     * @param row The tile row.
     * @param col The tile column.
     * @return The type of tile at (row, col).
     */
    public int getType(int row, int col) {
        if ((row < 0) || (col < 0) || (row >= numRows) || (col >= numCols)) {
            /* Space outside of the map is always passable */
            return Tile.PASSABLE;
        }

        int rc = mapStructure.get(col)[row];
        int rw = rc / numTilesAcross;
        int clmn = rc % numTilesAcross;
        return tiles[rw][clmn].getType();
    }

    protected Semaphore getMapStructureAvailable() { return this.mapStructureAvailable; }

    /**
     * Sets the map position. Is called in the update section of the GameState containing the tilemap.
     * @param x The new x of the tilemap.
     * @param y The new y of the tilemap.
     */
    public void setPosition(double x, double y) {
        /* Handles the map tween */
        this.x += (x - this.x);
        this.y += (y - this.y);

        fixBounds();

        /* Calculate column and row offest */
        colOffset = (int) - this.x / tileSize;
        rowOffset = (int) - this.y / tileSize;
    }

    /**
     * Make sure the camera does not not exceed the predefined minimum and maximum x and y values.
     */
    private void fixBounds() {
        if (x < xmin) x = xmin;
        if (x > xmax) x = xmax;
        if (y < ymin) y = ymin;
        if (y > ymax) y = ymax;
    }

    /**
     * This update method checks the map bounds and forks off a new thread to extend the map vector if necessary.
     */
    public void update() {
        /* If the column offset is such that there are less than 10 columns available off the right side of the screen */
        if (colOffset > (mapStructure.size() - ((GamePanel.WIDTH / GamePanel.TILESIZE) + 10))) {
            builder.setWorkLoad(10);
            builder.run();
        }
    }

    /**
     * Draw the tilemap. Reads from the protected MapStructure Vector. TODO: Add protection
     * @param g The Graphics2D object to draw into.
     */
    public void draw(Graphics2D g) {
        // Loop through every visible row
        for (
                int row = rowOffset;
                row < rowOffset + numRowsToRender;
                row++) {

            if (row >= numRows) break;

            for (
                    int col = colOffset;
                    col < colOffset + numColsToRender;
                    col++) {

                if (col >= numCols) break;

                /* Resolve tile in map datastructure */
                int tileToRender = mapStructure.get(col)[row];

                /* If the first tile in the tileset is encountered, skip it */
                if (tileToRender == 0) continue;

                /* Get tile y index in tileset */
                long r = tileToRender / numTilesAcross;
                /* Get tile x index in tileset */
                long c = tileToRender % numTilesAcross;

                g.drawImage(
                        /* Resolve tile indices in tileset and load corresponding image */
                        tiles[(int)r][(int)c].getImage(),
                        (int) x + col * tileSize,
                        (int) y + row * tileSize,
                        null
                );
            }
        }
    }

    public void reset() {
        this.x = 0;
        this.y = 0;
        this.colOffset = 0;
        this.rowOffset = 0;

        fixBounds();
    }

    /**
     * Appends a tile configuration to the end of the map and updates all class variables that depend on map width.
     * @param config The configuration to be appended,
     */
    protected synchronized void appendTileConfig(TileConfiguration config) {
        for (int[] col: config.getConfiguration()) {
            this.mapStructure.addElement(col);
        }
        numCols = mapStructure.size();
        width = numCols * tileSize;
        xmin = GamePanel.WIDTH - width;
    }

    /**
     * TODO: The return value of this function will be used inside the GSM to enter a loading state until the level
     * structure has been extended. May not be needed
     * @return
     */
    public boolean isLoading() {
        if (mapStructureAvailable.availablePermits() == 0) {
            /* The map is currently being updated */
            if (colOffset > (mapStructure.size() - ((GamePanel.WIDTH / GamePanel.TILESIZE) + 2))) {
                /* There are only two blocks available off the right side of the screen */
                return true;
            }
        }
        return false;
    }
}
