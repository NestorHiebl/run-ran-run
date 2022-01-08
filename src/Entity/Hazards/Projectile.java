package Entity.Hazards;

import Entity.Animation;
import Entity.EntityState;
import Entity.Hazards.Hazard;
import Entity.LethalDamageException;
import GameState.GameStateManager;
import Networking.WeatherData;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class Projectile extends Hazard {
    public Projectile(TileMap tm, GameStateManager gsm, WeatherData weatherData, double spawnX, double spawnY, double speed) {
        super(tm, gsm, weatherData);

        width = 30;
        height = 30;
        collisionHeight = 30;
        collisionWidth = 30;

        maxSpeed = speed;

        this.x = spawnX;
        this.y = spawnY;

        this.currentAction = EntityState.IDLE;

        /* Load the hazard sprites */
        BufferedImage[] sprites = new BufferedImage[2];
        try {
            /* Open the spritesheet */
            BufferedImage spriteSheet = ImageIO.read(new File(mapWeatherToSpriteSheet(this.weatherData)));

            /* Fill the sprite array, which only contains one animation in this case */
            for (int j = 0; j < 2; j++) {
                sprites[j] = spriteSheet.getSubimage(j * width, 0, width, height);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.animation = new Animation(sprites,16);
    }

    @Override
    public void update() {
        try {
            this.setPosition(this.getX() - this.maxSpeed, this.y);
        } catch (LethalDamageException e) {
            this.kill();
        }
        animation.update();

    }

    @Override
    public void draw(Graphics2D g) {

        g.drawImage(
                animation.getFrame(),
                (int) (x + tileMap.getX() - (width / 2)),
                (int) (y + tileMap.getY() - (height / 2)),
                null
        );
    }

    private String mapWeatherToSpriteSheet(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Ash":
                return "Resources/Sprites/Hazards/Projectile_cloudy.gif";
            case "Clear":
                return "Resources/Sprites/Hazards/Projectile_cloudy.gif";
            case "Clouds":
                return "Resources/Sprites/Hazards/Projectile_cloudy.gif";
            case "Thunderstorm":
            case "Drizzle":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Squall":
            case "Tornado":
            case "Rain":
                return "Resources/Sprites/Hazards/Projectile_cloudy.gif";
            default:
                return "Resources/Sprites/Hazards/Projectile_cloudy.gif";
        }
    }
}
