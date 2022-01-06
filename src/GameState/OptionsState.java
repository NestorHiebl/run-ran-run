package GameState;

import Networking.WeatherData;

import java.awt.*;

public class OptionsState extends GameState{

    public OptionsState(GameStateManager gsm, WeatherData weatherData) {
        super(StateType.OPTIONS, gsm, weatherData);
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {

    }

    @Override
    public void reload() {

    }

    @Override
    public void startWorkers() {

    }

    @Override
    public void keyPressed(int k) {

    }

    @Override
    public void keyReleased(int k) {

    }

    public void playBGM() {
    }

    public void stopBGM() {
    }
}
