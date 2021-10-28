package TileMap;

import Main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class TileMap {

    // Position
    private double x, y;

    // Bounds
    private int xmin, ymin, xmax, ymax;

    private double tween;

    // Map
    private int[][] map;
    private int tileSize;
    private int numRows, numCols;
    private int width, height;

    /***** Tileset *****/
    private BufferedImage tileset;
    // Width of the tileset
    private int numTilesAcross;
    private Tile[][] tiles;

    // Drawing
    private int rowOffset, colOffset;
    private int numRowsToRender, numColsToRender;


    public TileMap(int tileSize) {
        this.tileSize = tileSize;
        numRowsToRender = GamePanel.HEIGHT / tileSize + 2;
        numColsToRender = GamePanel.WIDTH / tileSize + 2;
        tween = 0.07;
    }

    public void loadTiles(String s) {
        try {
            tileset = ImageIO.read(new File(s));
            numTilesAcross = tileset.getWidth() / tileSize;
            // We assume that the image has to rows of tiles
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
                tiles[0][col] = new Tile(subimage, Tile.NORMAL);

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

    public int getType(int row, int col) {
        int rc = map[row][col];
        int rw = rc / numTilesAcross;
        int clmn = rc % numTilesAcross;
        return tiles[rw][clmn].getType();
    }

    public void setTween(double tween) {
        this.tween = tween;
    }

    public void setPosition(double x, double y) {
        this.x += (x - this.x) * tween;
        this.y += (y - this.y) * tween;

        fixBounds();

        colOffset = (int) - this.x / tileSize;
        rowOffset = (int) - this.y / tileSize;
    }

    private void fixBounds() {
        if (x < xmin) x = xmin;
        if (x > xmax) x = xmax;
        if (y < ymin) y = ymin;
        if (y > ymax) y = ymax;
    }

    public void draw(Graphics2D g) {
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

                int rc = map[row][col];
                // Get tile y index in tileset
                int r = rc / numTilesAcross;
                // Get tile x index in tileset
                int c = rc % numTilesAcross;

                g.drawImage(
                        // Resolve tile indices in tileset and load corresponding image
                        tiles[r][c].getImage(),
                        (int) x + col * tileSize,
                        (int) y + row * tileSize,
                        null
                );
            }
        }
    }

}
