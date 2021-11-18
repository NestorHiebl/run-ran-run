package GameState;

import Main.GamePanel;
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

    public MenuState(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    @Override
    public void init() {
        this.currentChoice = 0;
        try {
            bg = new Background("Resources/Backgrounds/menu_bg1.gif", -1);
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
        this.currentChoice = 0;
    }

    @Override
    public void update() {
        bg.update();
    }

    @Override
    public void draw(Graphics2D g) {
        bg.draw(g);

        // Draw title
        g.setColor(titleColor);
        g.setFont(titleFont);
        int titleWidth = calculateStringDisplayWidth(GamePanel.GAMETITLE);
        g.drawString(GamePanel.GAMETITLE, (GamePanel.WIDTH / 2) - (titleWidth / 2), 70);
        // Draw menu options
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if (i == currentChoice) {
                g.setColor(selectedColor);
            } else {
                g.setColor(menuItemColor);
            }
            int menuItemWidth = calculateStringDisplayWidth(options[i]);
            g.drawString(options[i], (GamePanel.WIDTH / 2) - (menuItemWidth / 4) /* No idea why this needs to be divided by four */, 140 + i *15);
        }
    }

    private void select() {
        switch (currentChoice) {
            case 0:
                // Start
                gsm.setState(StateType.LEVEL1);
                break;
            case 1:
                // Options
                break;
            case 2:
                // Quit
                System.exit(0);
                break;
            default:
                // Error
                System.exit(1);
                break;
        }
    }

    @Override
    public void keyPressed(int k) {
        if (k == KeyEvent.VK_ENTER) {
            select();
        }
        if (k == KeyEvent.VK_UP) {
            currentChoice--;
            // Wrap around to end of list
            currentChoice = Math.floorMod(currentChoice, options.length);
        }
        if (k == KeyEvent.VK_DOWN) {
            currentChoice++;
            // Wrap around to start of list
            currentChoice = Math.floorMod(currentChoice, options.length);
        }
    }

    @Override
    public void keyReleased(int k) {

    }

    /**
     * Calculates a string's display width given the current font options and the menu font render context.
     * Should be called immediately before the string is rendered to function correctly.
     * @param s The string whose width is to be queried
     * @return The render with of the given string
     */
    private int calculateStringDisplayWidth(String s) {
        return (int) titleFont.getStringBounds(s, this.frc).getWidth();
    }
}
