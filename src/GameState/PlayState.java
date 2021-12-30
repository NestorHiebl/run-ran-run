package GameState;

import Entity.Player;
import Main.GamePanel;
import TileMap.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayState extends GameState{

    private TileMap tileMap;
    private Background background;
    private Player player;

    public PlayState(GameStateManager gsm) {
        /* Send the game state type and manager to the parent class so they can be marked as final */
        super(StateType.PLAY, gsm);
        init();
    }

    @Override
    public void init() {
        // Load background and set its movement vector
        background = new Background("Resources/Backgrounds/placeholder-1.gif", -1);
        background.setVector(0.05, 0);

        // Load level tile map
        tileMap = new TileMap(30); /* The TileMap constructor already creates a tile structure */
        tileMap.loadTiles("Resources/Tilesets/basetileset.gif");

        // Load level structure
        tileMap.setPosition(0, 0);
        tileMap.setTween(1.0);

        // Load and place player entity
        player = new Player(tileMap, gsm);
        try {
            player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        background.update();
        player.update();
        tileMap.update();
        /* Center the player on the back third of the screen to provide more reaction time */
        double newMapX = (GamePanel.WIDTH / 3) - player.getX();
        double newMapY = (GamePanel.WIDTH / 2) - player.getY();
        tileMap.setPosition(newMapX, newMapY);
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw background
        background.draw(g);

        // Draw tilemap
        tileMap.draw(g);

        // Draw player
        if (player.getTransparent()) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            player.draw(g);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            player.draw(g);
        }

    }

    @Override
    public void reload() {
        tileMap.setPosition(0, 0);

        try {
            player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* TODO: forward key events to Player class */
    @Override
    public void keyPressed(int k) {
        switch (k) {
            case KeyEvent.VK_RIGHT:
                player.setRight(true);
                break;
            case KeyEvent.VK_UP:
                player.setUp(true);
                break;
            case KeyEvent.VK_DOWN:
                player.setDown(true);
                break;
            case KeyEvent.VK_A:
                player.setJumping();
                break;
            case KeyEvent.VK_S:
                player.setParrying();
                break;
        }
    }

    @Override
    public void keyReleased(int k) {
        switch (k) {
            case KeyEvent.VK_RIGHT:
                player.setRight(false);
                break;
            case KeyEvent.VK_UP:
                player.setUp(false);
                break;
            case KeyEvent.VK_DOWN:
                player.setDown(false);
                break;
        }
    }
}
