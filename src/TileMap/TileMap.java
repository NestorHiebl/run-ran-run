package TileMap;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class TileMap {

    // Position
    private double x, y;

    // Bounds
    private int xmin, ymin, xmax, ymax;

    private double tween;

    // Map
    private Vector<int[]> mapStructure;

    /* Will be replaced by the mapStructure */
    private int[][] map;
    private final int tileSize;
    private int numRows, numCols;
    private int width, height;

    /***** Tileset *****/
    private BufferedImage tileset;
    /* Width of the tileset */
    private int numTilesAcross;
    /* Game height in tiles. Is 8 with a pixel height of 240 and tile size of 30 */
    private final int numTilesVertical;
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
        tween = 0.07;
        numTilesVertical = GamePanel.HEIGHT / tileSize;

        this.mapStructure = new Vector<>();

        this.mapStructureAvailable = new Semaphore(1);

        this.builder = new MapBuilder(this, mapStructureAvailable);
        /* Run the builder a set number of times until there is enough initial map to go on */
        builder.setWorkLoad(30);
        builder.run();
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

    public void loadMap(String s) {
        try (BufferedReader br = new BufferedReader(new FileReader(s))) {

            // Read map file - the following file structure is arbitrary and will change
            numCols = Integer.parseInt(br.readLine()); // Line 1 holds the number of columns
            numRows = Integer.parseInt(br.readLine()); // Line 2 holds the number of rows
            System.out.printf("%d cols and %d rows found in %s\n", numCols, numRows, s);

            map = new int[numRows][numCols];
            width = numCols * tileSize;
            height = numRows * tileSize;

            xmin = GamePanel.WIDTH - width;
            xmax = 0;
            ymin = GamePanel.HEIGHT - height;
            ymax = 0;

            String delims = "\\s+";
            for (int row = 0; row < numRows; row++) {
                String line = br.readLine();
                String[] tokens = line.split(delims);
                for (int col = 0; col < numCols; col++) {
                    map[row][col] = Integer.parseInt(tokens[col]);
                    System.out.printf("%2d ", map[row][col]);
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTileSize() {
        return this.tileSize;
    }

    public int getx() {
        return (int) x;
    }

    public int gety() {
        return (int) y;
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
        int rc = map[row][col];
        int rw = rc / numTilesAcross;
        int clmn = rc % numTilesAcross;
        return tiles[rw][clmn].getType();
    }

    protected Semaphore getMapStructureAvailable() { return this.mapStructureAvailable; }

    public void setTween(double tween) {
        this.tween = tween;
    }

    /**
     * Sets the map position. Is called in the update section of the GameState containing the tilemap.
     * @param x The new x of the tilemap.
     * @param y The new y of the tilemap.
     */
    public void setPosition(double x, double y) {
        /* Handles the map tween */
        this.x += (x - this.x) * tween;
        this.y += (y - this.y) * tween;

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

                // If the first tile in the tileset is encountered, skip it
                if (map[row][col] == 0) continue;

                long rc = map[row][col];
                // Get tile y index in tileset
                long r = rc / numTilesAcross;
                // Get tile x index in tileset
                long c = rc % numTilesAcross;

                g.drawImage(
                        // Resolve tile indices in tileset and load corresponding image
                        tiles[(int)r][(int)c].getImage(),
                        (int) x + col * tileSize,
                        (int) y + row * tileSize,
                        null
                );
            }
        }
    }

    /**
     * TODO:
     * @param config
     */
    protected synchronized void appendTileConfig(TileConfigurations config) {
        for (int[] col: config.getConfiguration()) {
            this.mapStructure.addElement(col);
        }
    }

    /**
     * TODO: The return value of this function will be used inside the GSM to enter a loading state until the level
     * structure has been extended. What is the best way to code this?
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
