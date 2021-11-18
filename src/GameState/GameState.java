package GameState;

public abstract class GameState {
    final protected GameStateManager gsm;

    final protected StateType stateType;

    /**
     * Permanently set the game state type and the game state manager for this state.
     * @param stateType The type of game state.
     * @param gsm The game state manager.
     */
    public GameState(StateType stateType, GameStateManager gsm) {
        this.stateType = stateType;
        this.gsm = gsm;
    }

    /**
     * Executed once, when the state is constructed.
     */
    public abstract void init();

    /**
     * Executed once per frame, game state backend logic.
     */
    public abstract void update();

    /**
     * Update the game graphics based on current state.
     * @param g The Game's Graphics2D object.
     */
    public abstract void draw(java.awt.Graphics2D g);

    /**
     * Reload the game state in case of e.g. Error or Player death.
     */
    public abstract void reload();
    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);
}
