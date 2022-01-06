package Entity.Hazards;

import Entity.Entity;
import GameState.GameStateManager;
import TileMap.TileMap;

public abstract class Hazard extends Entity {

    public Hazard(TileMap tm, GameStateManager gsm) {
        super(tm, gsm);
    }

    @Override
    public void kill() {
        this.dead = true;
    }
}
