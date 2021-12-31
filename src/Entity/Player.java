package Entity;

import GameState.GameStateManager;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    private int health;
    private final int maxHealth;
    private boolean dead;

    // Parry button input field
    private boolean parrying;

    // Parry state regulation
    private boolean parryActive;
    private boolean parryCoolDown;
    private final long parryTimer;
    private final long parryCoolDownTimer;
    private long parryCounter;

    // Flinching state regulation
    private boolean flinching;
    private long flinchTimer;


    // Animation hash table
    private HashMap<EntityState, BufferedImage[]> sprites;


    public Player(TileMap tm, GameStateManager gsm) {
        super(tm, gsm);
        width = 30;
        height = 30;

        collisionWidth = 30;
        collisionHeight = 30;

        // Player velocity constants, to be tweaked with
        moveSpeed = 0.4;
        maxSpeed = 2.5;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -5.2;
        stopJumpSpeed = 0.3;
        facingRight = true;

        // Parry effect setup
        parryTimer = 20;
        parryCoolDownTimer = 40;
        parryActive = false;
        parryCoolDown = false;
        parryCounter = 0;

        // Fastfall setup
        fastFalling = false;
        fastFallSpeed = 8.0;

        health = maxHealth = 4;

        // Map player states to the number of frames in their animations
        final Map<EntityState, Integer> frameAmount = Map.of(
                EntityState.IDLE, 1,
                EntityState.WALKING, 2,
                EntityState.JUMPING, 1,
                EntityState.FALLING, 1,
                EntityState.FLINCHING, 3,
                EntityState.PARRYING, 2,
                EntityState.DEAD, 1); /* TODO: Update sprites and set actual frame amount of the dying animation */

        // Load the player sprites
        try {
            // Open the spritesheet
            BufferedImage spriteSheet = ImageIO.read(new File("Resources/Sprites/charsprite_placeholder.gif"));
            // Initialize an animation HashMap
            sprites = new HashMap<>();

            // Convert each row in the spritesheet into an array of buffered images that is used to initialize an animation
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

        // Set the player state to idle and stop the animation from looping
        currentAction = EntityState.IDLE;
        animation = new Animation(sprites.get(EntityState.IDLE), -1);
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isParrying() { return parryActive; }

    public double getRelativeScreenXPosition() {
        return x + xmap - (width / 2);
    }

    /**
     * Turn on the parrying input vector. It will be unset automatically in the update function.
     */
    public void setParrying() { this.parrying = true; }

    public void update() {
        // Update position
        getNextPosition();
        checkTileMapCollision();
        try {
            setPosition(xTemp, yTemp);
        } catch (LethalDamageException e) {
            this.kill();
        }


        // Set animation

        // The parry action takes precedence over all the others
        if (parrying) {
            // If the parry button is pressed, enter parry state UNLESS it's already active or on cooldown
            if (!parryActive && !parryCoolDown) {
                // Enter parry state and set the frame counter
                parryActive = true;
                parryCoolDown = false;
                parryCounter = 0;

                // Override player state
                currentAction = EntityState.PARRYING;

                // Set parry animation
                animation.setFrames(sprites.get(EntityState.PARRYING));
                animation.setDelay(25);
            }
        }
        // Clear the parry input vector
        parrying = false;

        // If the player is in active parry state, ignore animation and state changes
        if (!parryActive) {

            if (dy > 0) /* Going down */ {
                if (currentAction != EntityState.FALLING) {
                    currentAction = EntityState.FALLING;
                    animation.setFrames(sprites.get(EntityState.FALLING));
                    animation.setDelay(-1); /* The falling animation only has one sprite */
                }
            } else if (dy < 0) /* Going up */ {
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
                // Switch to idle state if all other options have been exhausted
                if (currentAction != EntityState.IDLE) {
                    currentAction = EntityState.IDLE;
                    animation.setFrames(sprites.get(EntityState.IDLE));
                    animation.setDelay(-1); /* The idle animation only has one sprite */
                }
            }
        } else {
            // If the player is in active parry state
            if (parryCounter <= parryTimer) {
                // Increment the parry counter
                parryCounter++;
            } else {
                // When the parry counter reaches a threshold, leave active parry state and commence cooldown
                parryActive = false;
                parryCoolDown = true;
                parryCounter = 0;
            }
        }

        // Increment the parry cooldown
        if (parryCoolDown) {
            if (parryCounter <= parryCoolDownTimer) {
                parryCounter++;
            } else {
                parryActive = false;
                parryCoolDown = false;
            }
        }

        // If the player is in parry state or cooldown, increment the parry counter

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
        dx += moveSpeed;
        if (dx >= maxSpeed) {
            dx = maxSpeed;
        }/* else {
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
        }*/

        // Fastfalling
        if (fastFalling) {
            jumping = false;
            falling = false;
            dy = fastFallSpeed;
        }

        // Jumping
        if (jumping && !falling) {
            jumping = false;
            dy = jumpStart;
            falling = true;
        }

        // Falling
        if (falling) {
            dy += fallSpeed;

            if (jumping) {
                /* Jump input vector is active, initiate fast-fall */
                jumping = false;
                falling = false;
                fastFalling = true;
            }

            if (dy > maxFallSpeed) {
                dy = maxFallSpeed;
            }
        }
    }

    /**
     * Dying currently only reloads the level
     */
    void kill() {
        this.currentAction = EntityState.DEAD;

        /* Reset parry state */
        this.parryActive = false;
        this.parryCounter = 0;
        this.parryCoolDown = false;

        /* TODO: After the death animation has been added, trigger the state reload when the animation has played once */

        this.gsm.reloadCurrentState();
    }

    private void resetInputVector() {
        this.jumping = false;
        this.parrying = false;
    }
}