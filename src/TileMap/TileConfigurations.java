package TileMap;

import Main.GamePanel;

import java.util.Vector;

/**
 * A tile configuration is a pre-loaded chunk of tiles that can be dynamically appended to a level's TileMap. The
 * elements of this enum all return Vectors of int arrays (of minimum length 40) via the getConfiguration() method.
 * The configurations are loaded from files inside the Resources/Maps directory. The .map configuration files can be
 * thought of as transposed matrices containing the integer tile IDs of the configuration. To get a feeling for how
 * they'd look as a map chunk, swap the rows and columns.
 */
public enum TileConfigurations {
    DEFAULT(),
    LEDGE();

    private final int configLength;
    private final Vector<int[]> configuration;
    private final boolean extendable;

    public Vector<int[]> getConfiguration() {
        return this.configuration;
    }

    public boolean getExtendable() {
        return this.extendable;
    }

    TileConfigurations() {
        configLength = (GamePanel.WIDTH / GamePanel.TILESIZE) * 2;
        configuration = new Vector<>(configLength);
        switch (this) {
            case DEFAULT:
                loadDefaultConfig();
                this.extendable = true;
                break;
            case LEDGE:
                this.extendable = false;
                break;
            default:
                /* This should never happen */
                loadDefaultConfig();
                this.extendable =false;
                throw new IllegalStateException("Non-existent enum state passed to constructor");
        }
    }

    private void loadDefaultConfig() {
        for (int i = 0; i < configLength; i++) {
            this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        }
    }

    private void loadLedgeConfig() {
        this.configuration.addElement(new int[] {0,  0,  0,  0,  0,  0,  0, 21});
        this.configuration.addElement(new int[] {0,  0,  0,  0,  0,  0,  0, 21});
        this.configuration.addElement(new int[] {0,  0,  0,  0,  0,  0, 21, 21});
        this.configuration.addElement(new int[] {0,  0,  0,  0,  0,  0, 21, 21});
        this.configuration.addElement(new int[] {0,  0,  0,  0,  0,  0, 21, 21});
        this.configuration.addElement(new int[] {0,  0,  0,  0,  0, 21, 21, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
        this.configuration.addElement(new int[] {0, 0, 0, 0, 0, 0, 0, 21});
    }

}
