package Main;

import Entity.Player;
import GameState.GameStateManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.Timer;
import java.util.TimerTask;

public class GamePanel extends javax.swing.JPanel implements Runnable, KeyListener {

    // Global game info
    public static final String GAMETITLE = "Scraper";

    // Dimensions
    public static final int WIDTH = 600;
    public static final int HEIGHT = 240;
    public static final int SCALE = 2;
    public static final int TILESIZE = 30;

    // Game thread
    private Thread thread;
    private boolean running;
    private final int FPS = 60;
    private long targetTime = 1000 / FPS;

    // Image
    private BufferedImage image;
    private Graphics2D g;

    // Game state Manager
    private GameStateManager gsm;

    private Player player;

    // The constructor sets some of the window properties
    public GamePanel() {
        super();

        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setFocusable(true);
        requestFocus();

    }

    public void init() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        running = true;

        GameStateManager.GameStateManagerBuilder builder = new GameStateManager.GameStateManagerBuilder(g);
        gsm = builder.getGsm();
    }

    public void run() {
        init();

        long start;
        long elapsed;
        long wait;

        // Game loop
        while (running) {
            start = System.nanoTime();

            update();
            draw();
            drawToScreen();

            elapsed = System.nanoTime() - start;

            wait = targetTime - nanosecondsToMilliseconds(elapsed);

            // The first wait period is always a large negative value for some reason
            if (wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        gsm.update();
    }

    private void draw() {
        gsm.draw(g);
    }

    private void drawToScreen() {
        Graphics2D g2 = (Graphics2D) getGraphics();
        g2.drawImage(image, 0,0,WIDTH * SCALE, HEIGHT * SCALE, null);
    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            addKeyListener(this);
            thread.start();
        }
    }


    public void keyTyped(KeyEvent e) {

    }
    // Propagates the keyEvent to whichever object the game state manager deems appropriate
    public void keyPressed(KeyEvent e) {
        gsm.keyPressed(e.getKeyCode());

    }
    // Propagates the keyEvent to whichever object the game state manager deems appropriate
    public void keyReleased(KeyEvent e) {
        gsm.keyReleased(e.getKeyCode());

    }

    private long nanosecondsToMilliseconds(long nanoseconds) {
        return nanoseconds / 1000000;
    }
}
