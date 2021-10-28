package GameState;
import java.util.ArrayList;

public class GameStateManager {
    private ArrayList<GameState> gameStates;
    private int currentState;

    public static final int MENUSTATE = 0;
    public static final int LEVEL1STATE = 1;

    public GameStateManager() {
        gameStates = new ArrayList<GameState>();

        currentState = MENUSTATE;
        // Currently, all levels are loaded into memory as soon as the game state manager is constructed.
        // This is not particularly efficient and might have to be changed at some point
        gameStates.add(new MenuState(this));
        gameStates.add(new Level1State(this));
    }

    public void setState(int state) {
        currentState = state;
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
