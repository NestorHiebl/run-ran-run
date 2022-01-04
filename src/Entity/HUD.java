package Entity;

import GameState.GameStateManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUD {
    private BufferedImage healthBarFrame;

    /* Location on screen */
    private final int x;
    private final int y;

    /* Dimensions of a single health point indicator */
    private final int healthBarWidth;
    private final int healthBarHeight;
    private final int healthBarDistance;

    /* Player object for grabbing health values */
    private final Player player;

    private final Color healthBarColor;

    private GameStateManager gsm;

    public HUD(GameStateManager gsm, Player player, int x, int y) {
        this.gsm = gsm;
        this.player = player;

        this.x = x;
        this.y = y;

        this.healthBarWidth = 30;
        this.healthBarHeight = 7;
        this.healthBarDistance = 4;

        this.healthBarColor = Color.WHITE;
    }

    public void draw(Graphics2D g) {
        g.setColor(healthBarColor);

        /* Draw each health point indicator */
        for (int i = 0; i < player.getHealth(); i++) {
            int barXPosition = x + (i * healthBarWidth) + (i * healthBarDistance);

            g.fillRect(barXPosition, y, healthBarWidth, healthBarHeight);
        }

        /* Draw heal indicator */
        if ((player.getHealth() < player.getMaxHealth()) && (player.getHealCounter() != 0)) {
            /* The heal indicator is transparent */
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            int healIndicatorXPosition = x + (player.getHealth() * healthBarWidth) + (player.getHealth() * healthBarDistance);
            int healBarWidth = (player.getHealCounter() / player.getHealThreshold()) * healthBarWidth;
            g.fillRect(healIndicatorXPosition, y, healBarWidth, healthBarHeight);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}
