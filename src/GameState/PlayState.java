package GameState;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Hazards.Hazard;
import Entity.Hazards.HazardSpawner;
import Entity.Hazards.Projectile;
import Main.GamePanel;
import Networking.WeatherData;
import TileMap.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayState extends GameState{

    private TileMap tileMap;
    private Background background;
    private Player player;
    private AudioPlayer BGM;
    private HUD hud;

    private HazardSpawner hazardSpawner;
    private ConcurrentLinkedQueue<Hazard> hazards;

    public PlayState(GameStateManager gsm, WeatherData weatherData) {
        /* Send the game state type and manager to the parent class so they can be marked as final */
        super(StateType.PLAY, gsm, weatherData);
        init();
    }

    @Override
    public void init() {
        /* Load background and set its movement vector */
        this.background = new Background(mapWeatherToBackground(this.weatherData));
        this.background.setVector(-0.05, 0);

        /* Load level tile map */
        this.tileMap = new TileMap(30, this.weatherData);
        this.tileMap.loadTiles(mapWeatherToTileSet(this.weatherData));

        /* Load level structure */
        this.tileMap.setPosition(0, 0);

        /* Load and place player entity */
        this.player = new Player(this.tileMap, this.gsm, this.weatherData);
        try {
            this.player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create hazard list */
        this.hazards = new ConcurrentLinkedQueue<Hazard>();

        /* Create hazard spawner */
        this.hazardSpawner = new HazardSpawner(this.gsm, this.tileMap, this, this.weatherData);

        this.hazards.add(new Projectile(tileMap, gsm, this.weatherData, 400, 195, 1.7));

        this.hazards.add(new Projectile(tileMap, gsm, this.weatherData, 800, 150, 2));

        /* Create HUD */
        this.hud = new HUD(this.gsm, this.player, 10, 10);

        /* Load BGM */
        this.BGM = new AudioPlayer(mapWeatherToBGM(this.weatherData));
    }

    @Override
    public void update() {
        /* Handle level acceleration */
        this.gsm.setScrollSpeed(this.gsm.getDefaultScrollSpeed() + this.calculateAcceleration());

        this.background.update();
        this.player.update();
        this.tileMap.update();

        /* Scroll the map by the amount determined by the GamePanel */
        double newMapX = this.tileMap.getX() - this.gsm.getScrollSpeed();
        double newMapY = (double) (GamePanel.WIDTH / 2) - this.player.getY();
        this.tileMap.setPosition(newMapX, newMapY);

        /* Update hazards */
        updateHazards();
    }

    @Override
    public void draw(Graphics2D g) {
        /* Draw background */
        this.background.draw(g);

        /* Draw tilemap */
        this.tileMap.draw(g);

        /* Draw player */
        if (this.player.isFlinching()) {
            /* Make player transparent while i-frames are active */
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            this.player.draw(g);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            this.player.draw(g);
        }

        drawHazards(g);

        this.hud.draw(g);
    }

    @Override
    public void reload() {
        this.stopBGM();

        /* Clear randomly generated map */
        this.tileMap.reset();

        /* Turn off hazard spawner */
        this.hazardSpawner.deactivate();

        /* Revert scroll speed to its minimum value */
        this.gsm.resetScrollSpeed();

        /* Update player score */
        this.gsm.setPreviousScore(this.getScore());

        /* Send player back to initial position */
        try {
            this.player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startWorkers() {
        this.playBGM();
        this.hazardSpawner.activate();
        this.hazardSpawner.start();
    }

    @Override
    public void keyPressed(int k) {
        switch (k) {
            case KeyEvent.VK_A:
                this.player.setJumping();
                break;
            case KeyEvent.VK_S:
                this.player.setParrying();
                break;
        }
    }

    @Override
    public void keyReleased(int k) {
        /* Releasing a key doesn't make a difference for us */
    }

    public void playBGM() {
        this.BGM.loopContinuously();
    }

    public void stopBGM() {
        this.BGM.stop();
    }

    public double getPlayerX() {
        return this.player.getX();
    }

    private void updateHazards() {
        for (Hazard h: this.hazards) {
            if (h.isDead()) {
                /* Remove dead hazards from level */
                this.hazards.remove(h);
            } else {
                h.update();
                /* Check for contact with player */
                if (h.intersects(this.player) && !this.player.isFlinching()) {
                    /* Check if the damage was parried */
                    if (this.player.isParrying()) {
                        this.player.heal();
                        h.kill();
                        this.gsm.requestFreezeFrame();
                    } else {
                        /* If the damage has not been parried, damage the player */
                        try {
                            this.player.damage();
                        } catch (LethalDamageException e) {
                            this.player.kill();
                        }
                    }
                }
            }
        }
    }

    private void drawHazards(Graphics2D g) {
        for (Hazard h: this.hazards) {
            if (!h.notOnScreen()) {
                h.draw(g);
            }
        }
    }

    public synchronized void spawnHazard(Hazard hazard) {
        this.hazards.add(hazard);
    }

    private String mapWeatherToBackground(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Ash":
                return "Resources/Backgrounds/placeholder-1.gif";
            case "Clear":
                return "Resources/Backgrounds/placeholder-1.gif";
            case "Clouds":
                return "Resources/Backgrounds/cloudy-bg.gif";
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
                return "Resources/Backgrounds/placeholder-1.gif";
        }
    }

    private String mapWeatherToTileSet(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Ash":
                return "Resources/Tilesets/placeholderset.gif";
            case "Clear":
                return "Resources/Tilesets/placeholderset.gif";
            case "Clouds":
                return "Resources/Tilesets/tilesset_clouds.gif";
            case "Thunderstorm":
            case "Drizzle":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Rain":
            case "Squall":
            case "Tornado":
            default:
                return "Resources/Tilesets/placeholderset.gif";
        }
    }

    private String mapWeatherToBGM(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Ash":
                return "Resources/Sound/Music/BGM_CLEAR.wav";
            case "Clear":
                return "Resources/Sound/Music/BGM_CLEAR.wav";
            case "Clouds":
                return "Resources/Sound/Music/BGM_CLEAR.wav";
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
                return "Resources/Sound/Music/BGM_CLEAR.wav";
        }
    }

    public double getScore() {
        return this.player.getX() / 10f;
    }

    private double calculateAcceleration() {
        double distance = this.player.getX() / 10f;

        double acceleration = 0;

        if (distance > 1000f) {

        }

        return acceleration;
    }
}
