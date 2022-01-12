package Main;

import GameState.GameStateManager;
import Networking.WeatherData;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class GamePanel extends javax.swing.JPanel implements Runnable, KeyListener {

    /* Global game info */
    public static final String GAMETITLE = "run ran run";
    private static boolean muted;

    /* Weather data container */
    private final WeatherData weatherData;

    /* Dimensions */
    public static final int WIDTH = 600;
    public static final int HEIGHT = 240;
    public static final int SCALE = 2;
    public static final int TILESIZE = 30;

    public static final double MIN_SCROLLSPEED = 2.2;
    public static final double MAX_SCROLLSPEED = 4.8;

    private final double scrollSpeed;

    /* Game thread */
    private Thread thread;
    private boolean running;
    private final int FPS = 60;

    /* Image */
    private BufferedImage image;
    private Graphics2D g;

    /* Game state Manager */
    private GameStateManager gsm;

    /* The constructor sets some of the window properties */
    public GamePanel(WeatherData weatherData) {
        super();

        GamePanel.muted = false;

        this.weatherData = weatherData;

        scrollSpeed = mapRange(-12f, 45f, MIN_SCROLLSPEED, MAX_SCROLLSPEED, this.weatherData.getTemp());

        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setFocusable(true);
        requestFocus();
    }

    public void init() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        running = true;

        GameStateManager.GameStateManagerBuilder builder = new GameStateManager.GameStateManagerBuilder(this.g, this.weatherData, this.scrollSpeed);
        gsm = builder.getGsm();
    }

    public void run() {
        init();

        long start;
        long elapsed;
        long wait;

        long targetTime = 1000 / FPS;

        /* Game loop - where the magic happens */
        while (running) {
            start = System.nanoTime();

                update();
                draw();
                drawToScreen();

            elapsed = System.nanoTime() - start;

            wait = targetTime - nanosecondsToMilliseconds(elapsed);

            /* The first wait period is always a large negative value for some reason */
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

    /* Propagates the keyEvent to whichever object the game state manager deems appropriate */
    public void keyPressed(KeyEvent e) {
        gsm.keyPressed(e.getKeyCode());
    }

    /* Propagates the keyEvent to whichever object the game state manager deems appropriate */
    public void keyReleased(KeyEvent e) {
        gsm.keyReleased(e.getKeyCode());
    }

    private long nanosecondsToMilliseconds(long nanoseconds) {
        return nanoseconds / 1000000;
    }

    public static void setMuted(boolean muted) {
        GamePanel.muted = muted;
    }

    public static boolean getMuted() {
        return GamePanel.muted;
    }

    public static double mapRange(double srcMin, double srcMax, double destMin, double destMax, double num) {
        return destMin + (((num - srcMin) * (destMax - destMin)) / (srcMax - srcMin));
    }

}
