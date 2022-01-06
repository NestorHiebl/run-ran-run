package GameState;

import Main.GamePanel;
import Networking.WeatherData;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;

public class GameOverState extends GameState {

    private int choiceIndex;
    private String[] options;

    private Color BGColor, menuItemColor, selectedColor;
    private Font font;
    private FontRenderContext frc;


    public GameOverState(GameStateManager gsm, WeatherData weatherData) {
        super(StateType.GAMEOVER, gsm, weatherData);
        init();
    }

    @Override
    public void init() {
        choiceIndex = 0;
        options = new String[2];
        options[0] = "again";
        options[1] = "quit";

        BGColor = Color.DARK_GRAY;
        menuItemColor = Color.RED;
        selectedColor = Color.WHITE;
        font = new Font("Arial", Font.PLAIN, 12);
        frc = new FontRenderContext(null, false, false);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(java.awt.Graphics2D g) {
        g.setColor(BGColor);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if (i == choiceIndex) {
                g.setColor(selectedColor);
            } else {
                g.setColor(menuItemColor);
            }
            int menuItemWidth = calculateStringDisplayWidth(options[i], this.font, this.frc);
            g.drawString(options[i], ((GamePanel.WIDTH / 3) * (i + 1)) - (menuItemWidth / 2), 140);
        }
        g.setColor(BGColor);

    }

    private void select() {
        gsm.getState(StateType.PLAY).reload();
        switch (choiceIndex) {
            case 0:
                gsm.setState(StateType.PLAY);
                break;
            case 1:
                gsm.getState(StateType.MAINMENU).reload();
                this.gsm.setState(StateType.MAINMENU);
                break;
            default:
                System.exit(2);
                break;
        }
    }

    @Override
    public void reload() {
        this.choiceIndex = 0;
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
            case KeyEvent.VK_LEFT:
                choiceIndex--;
                /* Wrap around to end of list */
                choiceIndex = Math.floorMod(choiceIndex, options.length);
                break;
            case KeyEvent.VK_RIGHT:
                choiceIndex++;
                /* Wrap around to start of list */
                choiceIndex = Math.floorMod(choiceIndex, options.length);
                break;
        }
    }

    @Override
    public void keyReleased(int k) {

    }

    @Override
    public void playBGM() {
    }

    @Override
    public void stopBGM() {
    }
}
