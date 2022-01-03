package TileMap;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class MapBuilder implements Runnable{
    private TileMap tileMap;
    private Semaphore semaphore;
    private Random RNG;

    private int workLoad;

    public MapBuilder(TileMap tileMap, Semaphore semaphore) {
        this.tileMap = tileMap;
        this.semaphore = semaphore;
        this.RNG = new Random();
        this.workLoad = 1;
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
            switch (RNG.nextInt(TileConfiguration.values().length)) {
                case 0:
                    tileMap.appendTileConfig(TileConfiguration.DEFAULT);
                    break;
                case 1:
                    tileMap.appendTileConfig(TileConfiguration.LEDGE);
                    break;
                case 2:
                    tileMap.appendTileConfig(TileConfiguration.BUMPS1);
                    break;
                default:
                    tileMap.appendTileConfig(TileConfiguration.DEFAULT);
                    break;
            }
        }

        this.workLoad = 1;


        /* Give the semaphore back */
        semaphore.release();
    }
}
