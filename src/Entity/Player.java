package Entity;

import Audio.AudioPlayer;
import GameState.GameStateManager;
import GameState.StateType;
import Main.GamePanel;
import Networking.WeatherData;
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

    /**
     * Amount of times since last heal that player has parried
     */
    private int healCounter;

    /**
     * Numer of parries required to heal
     */
    private final int healThreshold;

    /* Parry button input field */
    private boolean parrying;

    /* Parry state regulation */
    private boolean parryActive;
    private boolean parryCoolDown;
    private final long parryTimer;
    private final long parryCoolDownTimer;
    private long parryCounter;

    /* Flinching state regulation */
    private boolean flinching;
    private int flinchTimer;
    private final int flinchDuration;

    /* Weather data */
    private WeatherData weatherData;

    /* Animation hash table */
    private HashMap<EntityState, BufferedImage[]> sprites;

    /* Sound effects */
    private final AudioPlayer parrySFX;
    private final AudioPlayer damagedSFX;
    private final AudioPlayer jumpSFX;


    public Player(TileMap tm, GameStateManager gsm, WeatherData weatherData) {
        super(tm, gsm, weatherData);

        width = 30;
        height = 30;

        collisionWidth = 30;
        collisionHeight = 30;

        /* Player velocity constants, to be tweaked */
        moveSpeed = 0.4;
        maxSpeed = this.gsm.getScrollSpeed();
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -5.2;
        stopJumpSpeed = 0.3;
        facingRight = true;

        /* Parry effect setup */
        parryTimer = 20;
        parryCoolDownTimer = 40;
        parryActive = false;
        parryCoolDown = false;
        parryCounter = 0;

        /* Fastfall setup */
        fastFalling = false;
        fastFallSpeed = 8.0;

        health = maxHealth = 4;

        healCounter = 0;
        healThreshold = 5;

        /* Flinch parameters */
        flinchTimer = 0;
        flinchDuration = 60;
        flinching = false;

        dead = false;

        /* Map player states to the number of frames in their animations */
        final Map<EntityState, Integer> frameAmount = Map.of(
                EntityState.IDLE, 1,
                EntityState.WALKING, 2,
                EntityState.JUMPING, 1,
                EntityState.FALLING, 1,
                EntityState.FLINCHING, 3,
                EntityState.PARRYING, 2,
                EntityState.DEAD, 1);

        /* Load the player sprites */
        try {
            /* Open the spritesheet */
            BufferedImage spriteSheet = ImageIO.read(new File(mapWeatherToSpriteSheet(weatherData)));
            /* Initialize an animation HashMap */
            sprites = new HashMap<>();

            /* Convert each row in the spritesheet into an array of buffered images that is used to initialize an animation */
            for (EntityState state: EntityState.values()) {
                /* Make a new subarray of sprites for each animation */
                BufferedImage[] subSheet = new BufferedImage[frameAmount.get(state)];
                /* Fill the subarray */
                for (int j = 0; j < frameAmount.get(state); j++) {
                    subSheet[j] = spriteSheet.getSubimage(j * width, state.getIndex() * height, width, height);
                }
                sprites.put(state, subSheet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Set the player state to idle and stop the animation from looping */
        currentAction = EntityState.IDLE;
        animation = new Animation(sprites.get(EntityState.IDLE), -1);

        /* Load SFX */
        parrySFX = new AudioPlayer("Resources/Sound/SFX/Parry.wav");
        damagedSFX = new AudioPlayer("Resources/Sound/SFX/Hurt.wav");
        landingSFX = new AudioPlayer("Resources/Sound/SFX/Land.wav");
        jumpSFX = new AudioPlayer("Resources/Sound/SFX/Jump.wav");
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getHealCounter() { return healCounter; }
    public int getHealThreshold() { return healThreshold; }
    public boolean isParrying() { return parryActive; }
    public boolean isFlinching() { return flinching; }

    public double getX() {
        return this.x;
    }

    public double getRelativeScreenXPosition() {
        return x + tileMap.getX() - (double) (width / 2);
    }

    /**
     * Turn on the parrying input vector. It will be reset when appropriate in the update function.
     */
    public void setParrying() {
        this.parrying = true;
    }

    public void update() {
        /* Update max movement speed to account for acceleration */
        super.maxSpeed = this.gsm.getScrollSpeed();

        /* Update position */
        getNextPosition();
        checkTileMapCollision();
        try {
            setPosition(xTemp, yTemp);
        } catch (LethalDamageException e) {
            this.kill();
        }

        /* Handle i-frames after taking damage */
        if (flinching) {
            flinchTimer++;
            if (flinchTimer >= flinchDuration) {
                flinching = false;
                flinchTimer = 0;
            }

            if (flinchTimer == 1) {
                currentAction = EntityState.FLINCHING;

                animation.setFrames(sprites.get(EntityState.FLINCHING));
                animation.setDelay(4);
            }
        }


        /* Set animation */

        /* The parry action takes precedence over all the others */
        if (parrying) {
            /* If the parry button is pressed, enter parry state UNLESS it's already active or on cooldown */
            if (!parryActive && !parryCoolDown) {
                /* Enter parry state and set the frame counter */
                parryActive = true;
                parryCoolDown = false;
                parryCounter = 0;

                /* Override player state */
                currentAction = EntityState.PARRYING;

                /* Set parry animation */
                animation.setFrames(sprites.get(EntityState.PARRYING));
                animation.setDelay(3);
            }
        }
        /* Clear the parry input vector */
        parrying = false;

        /* If the player is in active parry state, ignore animation and state changes */
        if (!parryActive && !flinching) {

            if (dy > 0) /* Going down */ {
                if (currentAction != EntityState.FALLING) {
                    currentAction = EntityState.FALLING;
                    animation.setFrames(sprites.get(EntityState.FALLING));
                    animation.setDelay(-1);
                }
            } else if (dy < 0) /* Going up */ {
                if (currentAction != EntityState.JUMPING) {
                    currentAction = EntityState.JUMPING;
                    animation.setFrames(sprites.get(EntityState.JUMPING));
                    animation.setDelay(-1);
                }
            } else {
                if (currentAction != EntityState.WALKING) {
                    currentAction = EntityState.WALKING;
                    animation.setFrames(sprites.get(EntityState.WALKING));
                    animation.setDelay(3);
                }
            }
        } else {
            /* If the player is in active parry state */
            if (parryCounter <= parryTimer) {
                /* Increment the parry counter */
                parryCounter++;
            } else {
                /* When the parry counter reaches a threshold, leave active parry state and commence cooldown */
                parryActive = false;
                parryCoolDown = true;
                parryCounter = 0;
            }
        }

        /* Increment the parry cooldown */
        if (parryCoolDown) {
            if (parryCounter <= parryCoolDownTimer) {
                parryCounter++;
            } else {
                parryActive = false;
                parryCoolDown = false;
            }
        }

        animation.update();
    }

    @Override
    public void draw(Graphics2D g) {
            g.drawImage(
                    animation.getFrame(),
                    (int) (x + tileMap.getX() - (width / 2)),
                    (int) (y + tileMap.getY() - (height / 2)),
                    null
            );
    }

    private void getNextPosition() {
        /* Movement */

        /* Center the player on the back third of the screen to provide more reaction time */
        if (this.getRelativeScreenXPosition() < (double) GamePanel.WIDTH / 3) {
            this.maxSpeed = this.gsm.getScrollSpeed() * 1.3;
        } else {
            this.maxSpeed = this.gsm.getScrollSpeed();
        }

        dx += moveSpeed;
        if (dx >= maxSpeed) {
            dx = maxSpeed;
        }

        /* Fastfalling */
        if (fastFalling) {
            jumping = false;
            falling = false;
            dy = fastFallSpeed;
        }

        /* Jumping */
        if (jumping && !falling) {
            jumpSFX.play();
            jumping = false;
            dy = jumpStart;
            falling = true;
        }

        /* Falling */
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
     * Dying reloads the level structure from scratch and empties the hazard list. See the reload() method in
     * the PlayState class for more information.
     */
    @Override
    public void kill() {
        this.currentAction = EntityState.DEAD;

        /* Reset parry state */
        this.parryActive = false;
        this.parryCounter = 0;
        this.parryCoolDown = false;

        this.gsm.setState(StateType.GAMEOVER);

        this.health = this.maxHealth;
    }

    public void damage() throws LethalDamageException {
        damagedSFX.play();
        health--;
        if (health <= 0) {
            throw new LethalDamageException("Player took lethal damage");
        }
        flinching = true;
    }

    public void heal() {
        this.parrySFX.play();

        /* Reset parrying state */
        this.parrying = false;
        this.parryActive = false;
        this.parryCounter = 0;
        this.parryCoolDown = false;

        /* No healing takes place if the player is at max health */
        if (this.getHealth() == this.getMaxHealth()) {
            return;
        }

        this.healCounter++;

        /* Heal threshold has been reached */
        if (this.healCounter >= this.healThreshold) {
            /* Player health is not full */
            if (this.health < this.maxHealth) {
                /* Heal the player */
                this.health++;
            }
        }

        /* Wrap heal counter back around */
        this.healCounter = Math.floorMod(this.healCounter, this.healThreshold);
    }

    private String mapWeatherToSpriteSheet(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Ash":
                return "Resources/Sprites/charsprite_placeholder.gif";
            case "Clear":
                return "Resources/Sprites/charsprite_sun.gif";
            case "Clouds":
                return "Resources/Sprites/charsprite_clouds.gif";
            case "Thunderstorm":
            case "Drizzle":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Squall":
            case "Tornado":
            case "Rain":
            default:
                return "Resources/Sprites/charsprite_rain.gif";
        }
    }
}