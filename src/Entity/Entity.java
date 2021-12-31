package Entity;

import GameState.GameStateManager;
import Main.GamePanel;
import TileMap.*;

import java.awt.*;

public abstract class Entity {

    protected TileMap tileMap;
    protected GameStateManager gsm;
    protected int tileSize;
    protected double xmap;
    protected double ymap;

    /* Global position on tilemap */
    protected double x;
    protected double y;

    /* Global movement vector */
    protected double dx;
    protected double dy;

    /* Dimensions */
    protected int width;
    protected int height;

    /* Collision box */
    protected int collisionWidth;
    protected int collisionHeight;

    /* Collision detection */
    protected int currRow;
    protected int currCol;
    protected double xDest;
    protected double yDest;
    protected double xTemp;
    protected double yTemp;
    protected boolean topLeft;
    protected boolean topRight;
    protected boolean bottomLeft;
    protected boolean bottomRight;

    /* Animation */
    protected Animation animation;
    protected EntityState currentAction;
    protected boolean facingRight;
    protected boolean transparent;

    /* Movement */
    protected boolean left;
    protected boolean right;
    protected boolean up;
    protected boolean down;
    protected boolean jumping;
    protected boolean falling;
    protected boolean fastFalling;

    /* Movement attributes */
    protected double moveSpeed;
    protected double maxSpeed;
    protected double stopSpeed;
    protected double fallSpeed;
    protected double maxFallSpeed;
    protected double fastFallSpeed;
    protected double jumpStart;
    protected double stopJumpSpeed;

    public Entity(TileMap tm, GameStateManager gsm) {
        this.tileMap = tm;
        tileSize = tm.getTileSize();
        this.gsm = gsm;
        this.transparent = false;
    }

    public boolean intersects(Entity o) {
        Rectangle collision1 = this.getCollisionRect();
        Rectangle collision2 = o.getCollisionRect();

        return collision1.intersects(collision2);
    }

    public Rectangle getCollisionRect() {
        return new Rectangle((int) x - collisionWidth, (int) y - collisionHeight, collisionWidth, collisionHeight);
    }

    public void calculateCorners(double x, double y) {
        /* Get surrounding tile indices based on an entities x and y coordinates */
        int leftTileXIndex = (int) (x - collisionWidth / 2) / tileSize;
        int rightTileXIndex = (int) (x + (collisionWidth / 2) - 1) / tileSize;
        int topTileYIndex = (int) (y - collisionHeight / 2) / tileSize;
        int bottomTileYIndex = (int) (y + (collisionHeight / 2) - 1) / tileSize;

        /* Get corner tile types */
        int topLeftTileType = tileMap.getType(topTileYIndex, leftTileXIndex);
        int topRightTileType = tileMap.getType(topTileYIndex, rightTileXIndex);
        int bottomLeftTileType = tileMap.getType(bottomTileYIndex, leftTileXIndex);
        int bottomRightTileType = tileMap.getType(bottomTileYIndex, rightTileXIndex);

        /* Set corner booleans */
        this.topLeft = topLeftTileType == Tile.BLOCKED;
        this.topRight = topRightTileType == Tile.BLOCKED;
        this.bottomLeft = bottomLeftTileType == Tile.BLOCKED;
        this.bottomRight = bottomRightTileType == Tile.BLOCKED;
    }

    public void checkTileMapCollision() {
        currCol = (int) x / tileSize;
        currRow = (int) y / tileSize;

        xDest = x + dx;
        yDest = y + dy;

        xTemp = x;
        yTemp = y;

        calculateCorners(x, yDest); /* Check if y direction is blocked */
        if (dy < 0) /* Going up */ {
            if (topLeft || topRight) { /* Stop under tile we bumped our head into */
                dy = 0;
                yTemp = (currRow * tileSize) + (collisionHeight / 2.0);
            } else { /* Otherwise continue */
                yTemp += dy;
            }
        } else if (dy > 0) /* Going down */ {
            if (bottomLeft || bottomRight) { /* Stop moving once landed */
                dy = 0;
                falling = false;
                fastFalling = false;
                jumping = false;
                // Bracket order matters, kids!
                yTemp = ((currRow + 1) * tileSize) - (collisionHeight / 2.0);
            }else { /* Otherwise keep falling */
                yTemp += dy;
            }
        }

        calculateCorners(xDest, y); /* Check if x direction is blocked */
        if (dx < 0) /* Going left */ {
            if (topLeft || bottomLeft) {
                dx = 0;
                /* Set position just right of tile we moved into */
                xTemp = (currCol * tileSize) + (collisionWidth / 2.0);
            } else { /* Otherwise continue */
                xTemp += dx;
            }
        } else if (dx > 0) /* Going right */ {
            if (topRight || bottomRight) { /* Stop if the player moves into a tile */
                dx = 0;
                xTemp = ((currCol + 1) * tileSize) - (collisionWidth / 2.0);
            } else { /* Otherwise continue */
                xTemp += dx;
            }
        }

        if (!falling && !fastFalling) /* Walked off a cliff */ {
            calculateCorners(x, yDest + 1);
            if (!bottomLeft && !bottomRight) {
                falling = true;
            }
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCollisionWidth() { return collisionWidth; }
    public int getCollisionHeight() { return collisionHeight; }
    public boolean getTransparent() { return transparent; }

    public void setPosition(double x, double y) throws LethalDamageException{
        if (y > tileMap.getHeight()) {
            throw new LethalDamageException("Fell off the map");
        }

        this.x = x;
        this.y = y;
    }

    public void setVector(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void setMapPosition() {
        xmap = tileMap.getx();
        ymap = tileMap.gety();
    }


    /**
     * Is true if the Entity is moving left. In the player Entities case, this function is called
     * inside the Level state key event listener.
     * @param b Whether the Entity is moving left or not.
     */
    public void setLeft(boolean b) { this.left = b; }
    public void setRight(boolean b) { this.right = b; }
    public void setUp(boolean b) { this.up = b; }
    public void setDown(boolean b) { this.down = b; }
    public void setJumping() { this.jumping = true; }

    /**
     * Check in an Entity is within the screen bounds. If not, there is no need to render it.
     * @return true if on screen, false if not.
     */
    public boolean notOnScreen() {
        return  x + xmap + width < 0 ||
                x + xmap - width > GamePanel.WIDTH ||
                y + ymap + height < 0 ||
                y + ymap - height > GamePanel.HEIGHT;
    }

    abstract void kill();
}
