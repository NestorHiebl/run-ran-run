package Entity.Hazards;

import GameState.PlayState;
import Networking.WeatherData;

public class HazardSpawner implements Runnable {

    private WeatherData weatherData;

    private PlayState parentState;

    public HazardSpawner(PlayState parentState, WeatherData weatherData) {
        this.parentState = parentState;
        this.weatherData = weatherData;
    }

    @Override
    public void run() {

    }
}
