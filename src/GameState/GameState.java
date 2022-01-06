package GameState;

import Audio.AudioPlayer;
import Networking.WeatherData;

import java.awt.*;
import java.awt.font.FontRenderContext;

public abstract class GameState {
    final protected GameStateManager gsm;

    final protected StateType stateType;

    protected AudioPlayer BGM;

    protected WeatherData weatherData;

    /**
     * Permanently set the game state type and the game state manager for this state.
     * @param stateType The type of game state.
     * @param gsm The game state manager.
     */
    public GameState(StateType stateType, GameStateManager gsm, WeatherData weatherData) {
        this.stateType = stateType;
        this.gsm = gsm;
        this.weatherData = weatherData;
    }

    /**
     * Executed once, when the state is constructed.
     */
    public abstract void init();

    /**
     * Executed once per frame, game state backend logic.
     */
    public abstract void update();

    /**
     * Update the game graphics based on current state.
     * @param g The Game's Graphics2D object.
     */
    public abstract void draw(java.awt.Graphics2D g);

    /**
     * Is executed once every time the state is left.
     */
    public abstract void reload();

    /**
     * Is executed once every time the state is entered.
     */
    public abstract void startWorkers();

    public abstract void playBGM();

    public abstract void stopBGM();

    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);

    /**
     * Calculates a string's display width given the current font options and the menu font render context.
     * Should be called immediately before the string is rendered to function correctly.
     * @param s The string whose width is to be queried
     * @return The render with of the given string
     */
    int calculateStringDisplayWidth(String s, Font f, FontRenderContext frc) {
        return (int) f.getStringBounds(s, frc).getWidth();
    }
}
