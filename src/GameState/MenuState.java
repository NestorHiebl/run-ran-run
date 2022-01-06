package GameState;

import Main.GamePanel;
import Networking.WeatherData;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;

public class MenuState extends GameState{

    private Background bg;

    private int currentChoice;
    private String[] options = {
            "Start",
            "Options",
            "Quit"
    };

    private Color titleColor, menuItemColor, selectedColor;
    private Font titleFont;
    private Font font;
    private FontRenderContext frc;

    public MenuState(GameStateManager gsm, WeatherData weatherData) {
        /* Send the game state type and manager to the parent class so they can be marked as final */
        super(StateType.MAINMENU, gsm, weatherData);
        init();
    }

    @Override
    public void init() {
        this.currentChoice = 0;
        try {
            bg = new Background("Resources/Backgrounds/menu_bg1.gif");
            bg.setVector(-0.1, 0);

            titleColor = new Color(128, 0, 0);
            menuItemColor = Color.RED;
            selectedColor = Color.DARK_GRAY;
            titleFont = new Font("Century Gothic", Font.PLAIN, 28);
            font = new Font("Arial", Font.PLAIN, 12);
            frc = new FontRenderContext(null, false, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        this.stopBGM();
        this.currentChoice = 0;
    }

    @Override
    public void startWorkers() {

    }

    @Override
    public void update() {
        bg.update();
    }

    @Override
    public void draw(Graphics2D g) {
        bg.draw(g);

        /* Draw title */
        g.setColor(titleColor);
        g.setFont(titleFont);
        int titleWidth = calculateStringDisplayWidth(GamePanel.GAMETITLE, this.titleFont, this.frc);
        g.drawString(GamePanel.GAMETITLE, (GamePanel.WIDTH / 2) - (titleWidth / 2), 70);
        /* Draw menu options */
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(selectedColor);
            } else {
                g.setColor(menuItemColor);
            }
            int menuItemWidth = calculateStringDisplayWidth(options[i], this.font, this.frc);
            g.drawString(options[i], (GamePanel.WIDTH / 2) - (menuItemWidth / 2), 140 + i *15);
        }
    }

    private void select() {
        switch (currentChoice) {
            case 0:
                /* Start */
                gsm.setState(StateType.PLAY);
                break;
            case 1:
                /* Options */
                gsm.setState(StateType.OPTIONS);
                break;
            case 2:
                /* Quit */
                System.exit(0);
                break;
            default:
                /* Error */
                System.exit(1);
                break;
        }
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
}
