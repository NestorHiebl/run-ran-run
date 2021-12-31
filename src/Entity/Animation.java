package Entity;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int currentFrame;

    /**
     * Determines the length an animation subimage is displayed, in game frames.
     */
    private long delay;

    /**
     * The amount of frames a single subimage has been displayed for so far.
     */
    private long frameCounter;

    private boolean playedOnce;

    public Animation(BufferedImage[] frames) {
        if (frames == null) {
            throw new IllegalArgumentException("Cannot create an animation with null frame array");
        }
        this.frames = frames;
        this.currentFrame = 0;
        this.playedOnce = false;
        this.frameCounter = 0;
    }

    public void setFrames(BufferedImage[] frames) {
        if (frames == null) {
            throw new IllegalArgumentException("Cannot set animation frames with null frame array");
        }
        this.frames = frames;
        this.currentFrame = 0;
        this.playedOnce = false;
        this.frameCounter = 0;
    }

    /**
     * Creates a new animation from an array of buffered images, immediately setting its delay. The
     * delay is currently in milliseconds. If it is less than one, the animation will not progress, e.g.
     * it will remain on its first frame indefinitely.
     * @param frames
     * @param delay
     */
    public Animation(BufferedImage[] frames, long delay) {
        if (frames == null) {
            throw new IllegalArgumentException("Cannot create an animation with null frame array");
        }
        this.frames = frames;
        this.currentFrame = 0;
        this.playedOnce = false;
        this.delay = delay;
        this.frameCounter = 0;
    }

    /**
     * Set the animation delay, which is currently in milliseconds. If the delay is less than one, the
     * animation will not progress, e.g. it will remain on its first frame indefinitely.
     * @param d The delay in milliseconds.
     */
    public void setDelay(long d) { this.delay = d; }
    public void setFrame(int i) { this.currentFrame = i; }

    public void update() {
        if (delay < 1) return;

        frameCounter++;

        if (frameCounter > delay) {
            currentFrame++;
            frameCounter = 0;
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
