package Entity.Hazards;

import Entity.Entity;
import GameState.GameStateManager;
import Networking.WeatherData;
import TileMap.TileMap;

public abstract class Hazard extends Entity {

    public Hazard(TileMap tm, GameStateManager gsm, WeatherData weatherData) {
        super(tm, gsm, weatherData);
    }

    @Override
    public void kill() {
        this.dead = true;
    }
}
