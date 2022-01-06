package GameState;

import Networking.WeatherData;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;

public class OptionsState extends GameState{

    private int currentChoice;
    private String[] options;

    private Color menuItemColor, selectedColor;
    private Font font;
    private FontRenderContext frc;

    private Background bg;

    public OptionsState(GameStateManager gsm, WeatherData weatherData) {
        super(StateType.OPTIONS, gsm, weatherData);
        init();
    }

    @Override
    public void init() {
        currentChoice = 0;
        options = new String[]{"sound ", "quit"};

        menuItemColor = Color.RED;
        selectedColor = Color.WHITE;

        font = new Font("Arial", Font.PLAIN, 12);
        frc = new FontRenderContext(null, false, false);
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
        switch (k) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_A:
                select();
                break;
            case KeyEvent.VK_UP:
                currentChoice--;
                /* Wrap around to end of list */
                currentChoice = Math.floorMod(currentChoice, options.length);
                break;
            case KeyEvent.VK_DOWN:
                currentChoice++;
                /* Wrap around to start of list */
                currentChoice = Math.floorMod(currentChoice, options.length);
                break;
        }
    }

    @Override
    public void keyReleased(int k) {

    }

    public void playBGM() {
    }

    public void stopBGM() {
    }


    private void select() {

    }
}
