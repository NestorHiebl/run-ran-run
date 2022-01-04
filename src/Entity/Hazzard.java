package Entity;

import GameState.GameStateManager;
import TileMap.TileMap;

public class Hazzard extends Entity {

    private boolean damageActive;

    public Hazzard(TileMap tm, GameStateManager gsm) {
        super(tm, gsm);

        damageActive = false;
    }

    @Override
    void kill() {
        this.dead = true;
    }
}
