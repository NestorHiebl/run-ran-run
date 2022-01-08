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
        background = new Background(mapWeatherToBackground(this.weatherData));
        background.setVector(0.05, 0);

        /* Load level tile map */
        tileMap = new TileMap(30, this.weatherData);
        tileMap.loadTiles(mapWeatherToTileSet(this.weatherData));

        /* Load level structure */
        tileMap.setPosition(0, 0);

        /* Load and place player entity */
        player = new Player(tileMap, gsm, this.weatherData);
        try {
            player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create hazard list */
        hazards = new ConcurrentLinkedQueue<Hazard>();

        /* Create hazard spawner */
        hazardSpawner = new HazardSpawner(this.gsm, this.tileMap, this, this.weatherData);
        hazards.add(new Projectile(tileMap, gsm, 400, 195, 1.7));

        hazards.add(new Projectile(tileMap, gsm, 800, 150, 2));

        /* Create HUD */
        hud = new HUD(this.gsm, this.player, 10, 10);

        /* Load BGM */
        BGM = new AudioPlayer(mapWeatherToBGM(this.weatherData));
    }

    @Override
    public void update() {
        background.update();
        player.update();
        tileMap.update();

        /* Scroll the map by the amount determined by the GamePanel */
        double newMapX = tileMap.getX() - this.gsm.getScrollSpeed();
        double newMapY = (double) (GamePanel.WIDTH / 2) - player.getY();
        tileMap.setPosition(newMapX, newMapY);

        /* Update hazards */
        updateHazards();
    }

    @Override
    public void draw(Graphics2D g) {
        /* Draw background */
        background.draw(g);

        /* Draw tilemap */
        tileMap.draw(g);

        /* Draw player */
        if (player.isFlinching()) {
            /* Make player transparent while i-frames are active */
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            player.draw(g);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            player.draw(g);
        }

        drawHazards(g);

        hud.draw(g);
    }

    @Override
    public void reload() {
        this.stopBGM();
        tileMap.reset();

        hazardSpawner.deactivate();

        /* TODO: Reset level structure */

        try {
            player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startWorkers() {
        this.playBGM();
        hazardSpawner.activate();
        hazardSpawner.start();

    }

    @Override
    public void keyPressed(int k) {
        switch (k) {
            case KeyEvent.VK_A:
                player.setJumping();
                break;
            case KeyEvent.VK_S:
                player.setParrying();
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
        for (Hazard h: hazards) {
            if (h.isDead()) {
                /* Remove dead hazards from level */
                hazards.remove(h);
            } else {
                h.update();
                /* Check for contact with player */
                if (h.intersects(player) && !player.isFlinching()) {
                    /* Check if the damage was parried */
                    if (player.isParrying()) {
                        player.heal();
                        h.kill();
                        this.gsm.requestFreezeFrame();
                    } else {
                        /* If the damage has not been parried, damage the player */
                        try {
                            player.damage();
                        } catch (LethalDamageException e) {
                            player.kill();
                        }
                    }
                }
            }

        }
    }

    private void drawHazards(Graphics2D g) {
        for (Hazard h: hazards) {
            if (!h.notOnScreen()) {
                h.draw(g);
            }
        }
    }

    public synchronized void spawnHazard(Hazard hazard) {
        hazards.add(hazard);
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
                return "Resources/Tilesets/placeholderset.gif";
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
}
