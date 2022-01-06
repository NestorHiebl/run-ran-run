package Entity.Hazards;

import GameState.GameState;
import GameState.GameStateManager;
import GameState.PlayState;
import Main.GamePanel;
import Networking.WeatherData;
import TileMap.TileMap;

import java.util.Random;

public class HazardSpawner implements Runnable {

    private WeatherData weatherData;

    private PlayState parentState;
    private GameStateManager gsm;
    private TileMap tileMap;

    private int baseSpawnSpeed;

    private boolean active;

    private Random RNG;

    public HazardSpawner(GameStateManager gsm, TileMap tileMap, PlayState parentState, WeatherData weatherData) {
        this.parentState = parentState;
        this.gsm = gsm;
        this.tileMap = tileMap;
        this.weatherData = weatherData;

        this.RNG = new Random();

        active = false;

        baseSpawnSpeed = weatherData.getHumidity() * 75;
    }

    public synchronized void activate() {
        this.active = true;
    }

    public synchronized void deactivate() {
        this.active = false;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (active) {
            try {
                Thread.sleep(baseSpawnSpeed + RNG.nextInt(baseSpawnSpeed * 3));
            } catch (Exception e) {
                /* In case of an interrupt, print the exception and stop execution */
                e.printStackTrace();
                return;
            }

            double spawnXPosition = this.parentState.getPlayerX() + (double) (GamePanel.WIDTH);
            double spawnYPosition = (double) (RNG.nextInt(7) * GamePanel.TILESIZE);

            parentState.spawnHazard(new Projectile(this.tileMap, this.gsm, spawnXPosition, spawnYPosition));
        }

    }
}
