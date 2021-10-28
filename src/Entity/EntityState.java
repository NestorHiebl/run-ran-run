package Entity;

public enum EntityState {
    IDLE(0),
    WALKING(1),
    JUMPING(2),
    FALLING(3),
    FLINCHING(4),
    PARRYING(5);

    private final int index;

    EntityState(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }
}
