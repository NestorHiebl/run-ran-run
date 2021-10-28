package GameState;

import Entity.Player;
import Main.GamePanel;
import TileMap.*;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Level1State extends GameState{

    private TileMap tileMap;
    private Background background;
    private Player player;

    public Level1State(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    @Override
    public void init() {
        // Load background and set its movement vector
        background = new Background("Resources/Backgrounds/placeholder-1.gif", -1);
        background.setVector(0.05, 0);

        // Load level tile map
        tileMap = new TileMap(30);
        tileMap.loadTiles("Resources/Tilesets/basetileset.gif");

        // Load level structure
        tileMap.loadMap("Resources/Maps/level1-1.map");
        tileMap.setPosition(0, 0);
        tileMap.setTween(1.0);

        // Load and place player entity
        player = new Player(tileMap);
        player.setPosition(100, 100);
    }

    @Override
    public void update() {
        background.update();
        player.update();
        tileMap.setPosition((double) (GamePanel.WIDTH / 2) - player.getX(), (double) (GamePanel.HEIGHT / 2) - player.getY());
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw background
        background.draw(g);

        // Draw tilemap
        tileMap.draw(g);

        // Draw player
        player.draw(g);
    }

    @Override
    public void keyPressed(int k) {
        switch (k) {
            case KeyEvent.VK_LEFT:
                player.setLeft(true);
                break;
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
                player.setJumping(true);
                break;
            case KeyEvent.VK_S:
                player.setParrying(true);
                break;
        }
    }

    @Override
    public void keyReleased(int k) {
        switch (k) {
            case KeyEvent.VK_LEFT:
                player.setLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
                player.setRight(false);
                break;
            case KeyEvent.VK_UP:
                player.setUp(false);
                break;
            case KeyEvent.VK_DOWN:
                player.setDown(false);
                break;
            case KeyEvent.VK_A:
                player.setJumping(false);
                break;
            case KeyEvent.VK_S:
                player.setParrying(false);
                break;
        }
    }
}
