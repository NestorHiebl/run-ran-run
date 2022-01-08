package GameState;

import Audio.AudioPlayer;
import Main.GamePanel;
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

    private final AudioPlayer switchSelectionSFX;
    private final AudioPlayer selectSFX;

    public OptionsState(GameStateManager gsm, WeatherData weatherData) {
        super(StateType.OPTIONS, gsm, weatherData);
        init();

        switchSelectionSFX = new AudioPlayer("Resources/Sound/SFX/Select.wav");
        selectSFX = new AudioPlayer("Resources/Sound/SFX/Enter.wav");
    }

    @Override
    public void init() {
        currentChoice = 0;
        options = new String[]{"sound is ", "back"};

        menuItemColor = Color.RED;
        selectedColor = Color.WHITE;

        font = new Font("Arial", Font.PLAIN, 12);
        frc = new FontRenderContext(null, false, false);

        this.bg = new Background("Resources/Backgrounds/Options_bg.gif");
        bg.setVector(0,0);

    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics2D g) {
        bg.draw(g);
        g.setFont(font);
        String muteString = options[0] + (GamePanel.getMuted() ? "off" : "on");
        for (int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(selectedColor);
            } else {
                g.setColor(menuItemColor);
            }

            if (i == 0) {
                int menuItemWidth = calculateStringDisplayWidth(muteString, this.font, this.frc);
                g.drawString(muteString, (GamePanel.WIDTH / 2) - (menuItemWidth / 2), 140 + i * 15);
            } else {
                int menuItemWidth = calculateStringDisplayWidth(options[i], this.font, this.frc);
                g.drawString(options[i], (GamePanel.WIDTH / 2) - (menuItemWidth / 2), 140 + i * 15);
            }

        }
    }



    @Override
    public void keyPressed(int k) {
        switch (k) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_A:
                select();
                selectSFX.play();
                break;
            case KeyEvent.VK_UP:
                currentChoice--;
                switchSelectionSFX.play();
                /* Wrap around to end of list */
                currentChoice = Math.floorMod(currentChoice, options.length);
                break;
            case KeyEvent.VK_DOWN:
                currentChoice++;
                switchSelectionSFX.play();
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

    @Override
    public void reload() {
        currentChoice = 0;
    }

    @Override
    public void startWorkers() {

    }


    private void select() {
        switch (currentChoice) {
            case 0:
                toggleMuted();
                break;
            case 1:
                gsm.setState(StateType.MAINMENU);
                break;
            default:
                System.exit(-3);
                break;
        }
    }

    private void toggleMuted() {
        GamePanel.setMuted(!GamePanel.getMuted());
    }
}
