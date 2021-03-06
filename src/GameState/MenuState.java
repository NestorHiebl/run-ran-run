package GameState;

import Audio.AudioPlayer;
import Main.GamePanel;
import Networking.WeatherData;
import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;

public class MenuState extends GameState{

    private Background bg;

    private int currentChoice;
    private final String[] options = {
            "Start",
            "Options",
            "Quit"
    };

    private Color titleColor, menuItemColor, selectedColor;
    private Font titleFont;
    private Font font;
    private FontRenderContext frc;

    private final AudioPlayer switchSelectionSFX;
    private final AudioPlayer selectSFX;



    public MenuState(GameStateManager gsm, WeatherData weatherData) {
        /* Send the game state type and manager to the parent class so they can be marked as final */
        super(StateType.MAINMENU, gsm, weatherData);
        init();

        switchSelectionSFX = new AudioPlayer("Resources/Sound/SFX/Select.wav");
        selectSFX = new AudioPlayer("Resources/Sound/SFX/Enter.wav");
    }

    @Override
    public void init() {
        this.currentChoice = 0;
        try {
            bg = new Background(mapWeatherToMenuBackground(this.weatherData));
            bg.setVector(0, 0);

            titleColor = mapWeatherToTitleColor(this.weatherData);
            menuItemColor = mapWeatherToMenuItemColor(this.weatherData);
            selectedColor = Color.WHITE;
            titleFont = new Font("Century Gothic", Font.BOLD, 28);
            font = new Font("Arial", Font.PLAIN, 12);
            frc = new FontRenderContext(null, false, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        this.stopBGM();
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
        selectSFX.play();
    }

    @Override
    public void keyPressed(int k) {
        switch (k) {
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_A:
                select();
                break;
            case KeyEvent.VK_UP:
                switchSelectionSFX.play();
                currentChoice--;
                /* Wrap around to end of list */
                currentChoice = Math.floorMod(currentChoice, options.length);
                break;
            case KeyEvent.VK_DOWN:
                switchSelectionSFX.play();
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

    private String mapWeatherToMenuBackground(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Clear":
                return "Resources/Backgrounds/menu_bg_sun.gif";
            case "Clouds":
                return "Resources/Backgrounds/menu_bg_clouds.gif";
            case "Thunderstorm":
            case "Drizzle":
            case "Rain":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Ash":
            case "Squall":
            case "Tornado":
            default:
                return "Resources/Backgrounds/menu_bg_rain.gif";
        }
    }

    private Color mapWeatherToMenuItemColor(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Clear":
                return new Color(145, 79, 63);
            case "Clouds":
                return new Color(143, 129, 212);
            case "Thunderstorm":
            case "Drizzle":
            case "Rain":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Ash":
            case "Squall":
            case "Tornado":
            default:
                return new Color(0, 87, 237);
        }
    }

    private Color mapWeatherToTitleColor(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Clear":
                return new Color(130, 60, 50);
            case "Clouds":
                return new Color(117, 107, 166);
            case "Thunderstorm":
            case "Drizzle":
            case "Rain":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Ash":
            case "Squall":
            case "Tornado":
            default:
                return new Color(0, 87, 255);
        }
    }
}
