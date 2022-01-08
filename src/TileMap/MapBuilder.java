package TileMap;

import Networking.WeatherData;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class MapBuilder implements Runnable{
    private final TileMap tileMap;
    private final Semaphore semaphore;
    private final Random RNG;

    /* Weather data container for RNG seeding and chunk selection */
    private final WeatherData weatherData;

    private TileConfiguration[] availableConfigurations;

    private int workLoad;

    public MapBuilder(TileMap tileMap, Semaphore semaphore, WeatherData weatherData) {
        this.tileMap = tileMap;
        this.semaphore = semaphore;
        this.RNG = new Random();
        this.workLoad = 1;
        this.weatherData = weatherData;

        this.availableConfigurations = TileConfiguration.getAvailableConfigsForWeather(this.weatherData);
    }

    public synchronized void setWorkLoad(int i) {
        if (i < 1 || i > 100) {
            throw new IllegalArgumentException("Invalid workload specifier given to map builder");
        }

        this.workLoad = i;
    }

    /**
     * Spawns a thread that adds x number of chunks to the tilemap where x is defined by the workLoad variable. After
     * the thread is finished, workload is reset to 1.
     */
    @Override
    public void run() {
        /* Block the tilemap config vector semaphore */
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < this.workLoad; i++) {
            /* Rudimentary add - one of available configs, randomly */
            int configIndex = RNG.nextInt(availableConfigurations.length);
            tileMap.appendTileConfig(availableConfigurations[configIndex]);
        }

        this.workLoad = 1;


        /* Give the semaphore back */
        semaphore.release();
    }

}
