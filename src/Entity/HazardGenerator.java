package Entity;

import GameState.GameStateManager;
import GameState.PlayState;

public class HazardGenerator implements Runnable {
    private final PlayState level;

    private final GameStateManager gsm;

    public HazardGenerator(PlayState level, GameStateManager gsm) {
        this.level = level;
        this.gsm = gsm;
    }

    @Override
    public void run() {

    }

}
