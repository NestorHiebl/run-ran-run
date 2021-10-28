package Entity;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    private int health;
    private int maxHealth;
    private boolean dead;
    private boolean flinching;
    private long flinchTime;
    private boolean parrying;
    private long parryTimer;

    double xVelocity, yVelocity;

    /* Input vectors */
    boolean keyLeft;
    boolean keyRight;
    boolean keyUp;
    boolean keyDown;
    boolean Parry;

    // Animations
    // TODO: Refactor sprites into a hashmap
    private HashMap<EntityState, BufferedImage[]> sprites;

    // Animation indices
    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 3;
    private static final int FLINCHING = 4;
    private static final int PARRYING = 5;

    // Number of frames per animation
    private final Map<EntityState, Integer> frameAmount = Map.of(
            EntityState.IDLE, 1,
            EntityState.WALKING, 2,
            EntityState.JUMPING, 1,
            EntityState.FALLING, 1,
            EntityState.FLINCHING, 3,
            EntityState.PARRYING, 2);


    public Player(TileMap tm) {
        super(tm);
        width = 30;
        height = 30;

        collisionWidth = 30;
        collisionHeight = 30;

        moveSpeed = 0.3;
        maxSpeed = 2.0;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;
        facingRight = true;

        health = maxHealth = 4;

        // Load sprites
        try {
            BufferedImage spriteSheet = ImageIO.read(new File("Resources/Sprites/charsprite_placeholder.gif"));
            sprites = new HashMap<>();

            for (EntityState state: EntityState.values()) {
                // Make a new subarray of sprites for each animation
                BufferedImage[] subSheet = new BufferedImage[frameAmount.get(state)];
                // Fill the subarray
                for (int j = 0; j < frameAmount.get(state); j++) {
                    subSheet[j] = spriteSheet.getSubimage(j * width, state.getIndex() * height, width, height);
                }
                sprites.put(state, subSheet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        animation = new Animation();
        currentAction = EntityState.IDLE;
        animation.setFrames(sprites.get(EntityState.IDLE));
        animation.setDelay(-1);
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public void setParrying(boolean b) { this.parrying = b; }

    public void update() {
        // Update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xTemp, yTemp);

        // Set animation

        if (dy > 0) /* Going down */ {
            if (currentAction != EntityState.FALLING) {
                currentAction = EntityState.FALLING;
                animation.setFrames(sprites.get(EntityState.FALLING));
                animation.setDelay(-1); /* The falling animation only has one sprite */
            }
        } else if (dx < 0) /* Going up */ {
            if (currentAction != EntityState.JUMPING) {
                currentAction = EntityState.JUMPING;
                animation.setFrames(sprites.get(EntityState.JUMPING));
                animation.setDelay(-1); /* The jumping animation only has one sprite */
            }
        } else if (left || right) {
            if (currentAction != EntityState.WALKING) {
                currentAction = EntityState.WALKING;
                animation.setFrames(sprites.get(EntityState.WALKING));
                animation.setDelay(50);
            }
        } else {
            if (currentAction != EntityState.IDLE) {
                currentAction = EntityState.IDLE;
                animation.setFrames(sprites.get(EntityState.IDLE));
                animation.setDelay(-1); /* The idle animation only has one sprite */
            }
        }


        if (parrying) {
            if (currentAction != EntityState.PARRYING) {
                currentAction = EntityState.PARRYING;
                animation.setFrames(sprites.get(EntityState.PARRYING));
                animation.setDelay(50);
            }
        }

        animation.update();

        // Check direction
        if (currentAction != EntityState.PARRYING) {
            if(right) {
                facingRight = true;
            } else if (left) {
                facingRight = false;
            }
        }
    }

    public void draw(Graphics2D g) {
        setMapPosition();

        if (facingRight) {
            g.drawImage(
                    animation.getFrame(),
                    (int) (x + xmap - (width / 2)),
                    (int) (y + ymap - (height / 2)),
                    null
            );
        } else {
            g.drawImage(
                    animation.getFrame(),
                    (int) (x + xmap - (width / 2) + width),
                    (int) (y + ymap - (height / 2)),
                    -width,
                    height,
                    null
            );
        }

    }

    private void getNextPosition() {
        // Movement
        if(left) {
            dx -= moveSpeed;
            if (dx <= -maxSpeed) {
                dx = -maxSpeed;
            }
        } else if (right) {
            dx += moveSpeed;
            if (dx >= maxSpeed) {
                dx = maxSpeed;
            }
        } else {
            /* Stopping mechanics */
            if (dx > 0) {
                dx -= stopSpeed;
                if (dx < 0) {
                    dx = 0;
                }
            } else if (dx < 0) {
                dx += stopSpeed;
                if (dx > 0) {
                    dx = 0;
                }
            }
        }

        // Jumping
        if (jumping && !falling) {
            dy = jumpStart;
            falling = true;
        }

        // Falling
        if (falling) {
            dy += fallSpeed;

            if (dy > 0) {
                /* Exit jumping state once upwards acceleration is used up */
                jumping = false;
            }

            if (dy > maxFallSpeed) {
                dy = maxFallSpeed;
            }

        }
    }
}
