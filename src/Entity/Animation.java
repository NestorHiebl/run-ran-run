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

    public Animation(BufferedImage[] frames) {
        if (frames == null) {
            throw new IllegalArgumentException("Cannot create an animation with null frame array");
        }
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
    }

    public void setFrames(BufferedImage[] frames) {
        if (frames == null) {
            throw new IllegalArgumentException("Cannot set animation frames with null frame array");
        }
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
    }

    /**
     * Creates a new animation from an array of buffered images, immediately setting its delay. The
     * delay is currently in milliseconds. If it is less than one, the animation will not progress, e.g.
     * it will remain on its first frame indefinitely.
     * todo: Change milliseconds into frames
     * @param frames
     * @param delay
     */
    public Animation(BufferedImage[] frames, long delay) {
        if (frames == null) {
            throw new IllegalArgumentException("Cannot create an animation with null frame array");
        }
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
        playedOnce = false;
        // Set delay
        this.delay = delay;
    }

    /**
     * Set the animation delay, which is currently in milliseconds. If the delay is less than one, the
     * animation will not progress, e.g. it will remain on its first frame indefinitely.
     * todo: Change milliseconds into frames
     * @param d The delay in milliseconds.
     */
    public void setDelay(long d) { this.delay = d; }
    public void setFrame(int i) { this.currentFrame = i; }

    public void update() {
        if (delay < 1) return;

        // This processes animations separately from the global game clock, which is is not at all ideal
        // Todo: Bind animation frame processing to the global frame counter in GamePanel
        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if (elapsed > delay) {
            currentFrame++;
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
