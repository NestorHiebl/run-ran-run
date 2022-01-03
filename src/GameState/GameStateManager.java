package GameState;
import Main.GamePanel;

import java.awt.*;
import java.util.HashMap;
import javax.sound.sampled.*;

public class GameStateManager {
    private final Graphics2D g;

    private final HashMap<StateType, GameState> gameStates;
    private StateType previousState;
    private StateType currentState;

    private boolean transitioning;
    private int transitionCounter;
    private final static int transitionLength = 40;

    public static class GameStateManagerBuilder {

        GameStateManager gsm;
        Graphics2D g;
        private static boolean instantiated = false;

        public GameStateManagerBuilder(Graphics2D g) {
            if (instantiated) {
                throw new ExceptionInInitializerError("Only one instance of GameStateManager(Builder) may exist.");
            }
            this.gsm = null;
            this.g = g;
            instantiated = true;
        }

        public GameStateManager getGsm() {
            if (this.gsm == null) {
                this.gsm = new GameStateManager(this, g);
            }

            return this.gsm;
        }
    }

    private GameStateManager(GameStateManagerBuilder gsmB, Graphics2D g) {
        this.g = g;
        gameStates = new HashMap <StateType, GameState>();

        transitioning = false;
        transitionCounter = 0;

        currentState = StateType.MAINMENU;
        /* Currently, all levels are loaded into memory as soon as the game state manager is constructed. */
        gameStates.put(StateType.MAINMENU, new MenuState(this));
        gameStates.put(StateType.PLAY, new PlayState(this));

    }

    public void setState(StateType state) {
        this.gameStates.get(this.currentState).stopBGM();
        this.previousState = currentState;
        this.currentState = state;
        transitionState();
        this.gameStates.get(this.currentState).playBGM();
    }

    public void reloadCurrentState() {
        this.gameStates.get(this.currentState).stopBGM();
        this.gameStates.get(this.currentState).reload();
        transitionState();
        this.gameStates.get(this.currentState).playBGM();
    }

    /**
     * Update the current game state if not currently in a transition. Also handles exiting the transition state.
     */
    public void update() {
        if (!transitioning) {
            gameStates.get(currentState).update();
        } else {
            if (transitionCounter > transitionLength) {
                transitioning = false;
                transitionCounter = 0;
            }
        }
    }

    /**
     * Draw the current game state. Also handles transition graphics, as well as incrementing the transition state.
     * @param g The game's Graphics2D object.
     */
    public void draw(java.awt.Graphics2D g) {
        if (transitioning) {
            drawTransitionAnimation(transitionCounter, transitionLength);
            transitionCounter++;
        } else {
            gameStates.get(currentState).draw(g);
        }

    }

    /**
     * Key events are propagated down from the GamePanel class down to the concrete game state
     */
    public void keyPressed(int k) {
        if (!transitioning) {
            gameStates.get(currentState).keyPressed(k);
        }
    }

    /**
     * Key events are propagated down from the GamePanel class down to the concrete game state
     */
    public void keyReleased(int k) {
        gameStates.get(currentState).keyReleased(k);
    }

    private void transitionState() {
        this.transitioning = true;
        this.transitionCounter = 0;
    }

    /**
     * Draw a specific frame of the game state transition animation based on the transition  counter and duration. The
     * current animation is a rolling black screen, implemented here as a cascade of black bars.
     * @param transitionCounter The current frame of the transition.
     * @param transitionLength The total length of the transition.
     */
    private void drawTransitionAnimation(int transitionCounter, int transitionLength) {
        if ((transitionCounter < 0) || (transitionLength <= 0)) {
            throw new IllegalArgumentException("Invalid transition animation parameters");
        }

        if (transitionCounter < transitionLength / 2) {
            /* We're in the first half of the transition */
            g.setColor(Color.BLACK);

            /* Draw a new vertical bar every for frame until the screen is filled */
            int rectX = (GamePanel.WIDTH / (transitionLength / 2)) * transitionCounter;
            g.fillRect(rectX, 0, GamePanel.WIDTH / (transitionLength / 2), GamePanel.HEIGHT);

        } else if (transitionCounter < transitionLength) {
            /* Second stage of the transition */
            /* Draw the first frame of the new state under the transition animation */
            gameStates.get(currentState).draw(g);

            /* Cover the screen with a black rectangle that shrinks left-to-right until the image is fully visible */
            int rectX = GamePanel.WIDTH / (transitionLength / 2) * (transitionCounter - (transitionLength / 2));
            g.fillRect(rectX, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        }
    }
}
