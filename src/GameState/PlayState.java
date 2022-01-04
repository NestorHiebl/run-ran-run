package GameState;

import Audio.AudioPlayer;
import Entity.Hazard;
import Entity.LethalDamageException;
import Entity.Player;
import Main.GamePanel;
import TileMap.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class PlayState extends GameState{

    private TileMap tileMap;
    private Background background;
    private Player player;
    private AudioPlayer BGM;

    private ArrayList<Hazard> hazards;

    public PlayState(GameStateManager gsm) {
        /* Send the game state type and manager to the parent class so they can be marked as final */
        super(StateType.PLAY, gsm);
        init();
    }

    @Override
    public void init() {
        /* Load background and set its movement vector */
        background = new Background("Resources/Backgrounds/placeholder-1.gif", -1);
        background.setVector(0.05, 0);

        /* Load level tile map */
        tileMap = new TileMap(30);
        tileMap.loadTiles("Resources/Tilesets/basetileset.gif");

        /* Load level structure */
        tileMap.setPosition(0, 0);

        /* Load and place player entity */
        player = new Player(tileMap, gsm);
        try {
            player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Load BGM */
        BGM = new AudioPlayer("Resources/Sound/Music/Shutter2.wav");
    }

    @Override
    public void update() {
        background.update();
        player.update();
        tileMap.update();

        /* Scroll the map by the amount determined by the GamePanel */
        double newMapX = tileMap.getX() - GamePanel.SCROLLSPEED;
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
        tileMap.reset();

        /* TODO: Reset level structure */

        try {
            player.setPosition(100, 195);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(int k) {
        switch (k) {
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
        /* Releasing a key doesn't make a difference for us */
    }

    public void playBGM() {
        this.BGM.play();
    }

    public void stopBGM() {
        this.BGM.stop();
    }

    private void updateHazards() {
        for (Hazard h: hazards) {
            if (h.isDead()) {
                /* Remove dead hazards from level */
                hazards.remove(h);
            } else {
                h.update();
                /* Check for contact with player */
                if (h.intersects(player)) {
                    /* Check if the damage was parried */
                    if (player.isParrying()) {
                        player.heal();
                        h.kill();
                    } else {
                        /* If the damage has not been parried, damage the player */
                        try {
                            player.damage();
                        } catch (LethalDamageException e) {
                            player.kill();
                        }
                    }
                }
            }

        }
    }
}
