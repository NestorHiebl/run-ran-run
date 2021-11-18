package GameState;

public abstract class GameState {
    protected GameStateManager gsm;

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
