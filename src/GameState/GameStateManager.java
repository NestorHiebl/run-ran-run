package GameState;
import java.util.ArrayList;
import java.util.HashMap;

public class GameStateManager {
    private HashMap<StateType, GameState> gameStates;
    private StateType previousState;
    private StateType currentState;

    public static class GameStateManagerBuilder {

        GameStateManager gsm;
        private static boolean instantiated = false;

        public GameStateManagerBuilder() {
            if (instantiated) {
                throw new ExceptionInInitializerError("Only one instance of GameStateManager(Builder) may exist.");
            }
            this.gsm = null;
            instantiated = true;
        }

        public GameStateManager getGsm() {
            if (this.gsm == null) {
                this.gsm = new GameStateManager(this);
            }

            return this.gsm;
        }
    }

    private GameStateManager(GameStateManagerBuilder gsmB) {
        gameStates = new HashMap <StateType, GameState>();

        currentState = StateType.MAINMENU;
        // Currently, all levels are loaded into memory as soon as the game state manager is constructed.
        // This is not particularly efficient and might have to be changed at some point
        gameStates.put(StateType.MAINMENU, new MenuState(this));
        gameStates.put(StateType.LEVEL1, new Level1State(this));

    }

    public void setState(StateType state) {
        previousState = currentState;
        currentState = state;
    }

    public void reloadCurrentState() {
        this.gameStates.get(this.currentState).reload();
    }

    public void update() {
        gameStates.get(currentState).update();
    }

    public void draw(java.awt.Graphics2D g) {
        gameStates.get(currentState).draw(g);
    }

    // Key events are propagated down from the GamePanel class
    public void keyPressed(int k) {
        gameStates.get(currentState).keyPressed(k);
    }

    // Key events are propagated down from the GamePanel class
    public void keyReleased(int k) {
        gameStates.get(currentState).keyReleased(k);
    }
}
