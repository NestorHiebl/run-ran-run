package Entity;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int currentFrame;

    private long startTime;
    private long delay;

    private boolean playedOnce;

    public Animation() {
        playedOnce = false;
    }

    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
    }

    public void setDelay(long d) { this.delay = d; }
    public void setFrame(int i) { this.currentFrame = i; }

    public void update() {
        if (delay == -1) return;

        // This processes animations separately from the global game clock, which is is not at all ideal
        // Todo: Bind animation frame processing to the global frame counter in GamePanel
        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if (elapsed > delay) {
            currentFrame ++;
            startTime = System.nanoTime();
        }

        if (currentFrame == frames.length) {
            currentFrame = 0;
            playedOnce = true;
        }
    }

    public int getFrameNumber() { return currentFrame; }
    public BufferedImage getFrame() {return frames[currentFrame]; }
    public boolean hasPlayedOnce() { return playedOnce; }
}
