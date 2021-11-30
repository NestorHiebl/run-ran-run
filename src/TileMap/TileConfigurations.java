package TileMap;

import Main.GamePanel;

import java.io.BufferedReader;
import java.io.FileReader;
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

    private int configLength;
    private final int configHeight;
    private final Vector<int[]> configuration;
    private boolean extendable;

    public Vector<int[]> getConfiguration() {
        return this.configuration;
    }

    public boolean getExtendable() {
        return this.extendable;
    }

    public int getLength() {
        return this.configLength;
    }

    TileConfigurations() {
        configHeight = GamePanel.HEIGHT / GamePanel.TILESIZE;

        configuration = new Vector<>();
        switch (this) {
            case DEFAULT:
                loadDefaultConfig();
                break;
            case LEDGE:
                loadLedgeConfig();
                break;
            default:
                /* This should never happen */
                loadDefaultConfig();
                throw new IllegalStateException("Non-existent enum state passed to constructor");
        }
    }

    private void loadDefaultConfig() {
        loadMapFile("Resources/Maps/tileConfigLedge.map");
        this.configLength = configuration.size();
        this.extendable = true;
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
        this.extendable = false;
    }

    private void loadMapFile(String s) {
        try (BufferedReader br = new BufferedReader(new FileReader(s))) {

            // Read map file
            int numCols = Integer.parseInt(br.readLine()); // Line 1 holds the number of columns
            int numRows = Integer.parseInt(br.readLine()); // Line 2 holds the number of rows

            if (numRows != configHeight) throw new IllegalArgumentException("Invalid number of rows in map file");

            /* Delimiter regex. Matches any whitespace character (\s) repeated any number of times (+) */
            String delims = "\\s+";
            for (int col = 0; col < numCols; col++) {
                String line = br.readLine();

                /* An array containing a single map column, the elements have to be converted to integers before being
                * added to the config vector */
                String[] tokens = line.split(delims);
                if (tokens.length != configHeight) throw new IllegalArgumentException("Invalid number of rows in map file");

                /* Build a valid integer array */
                int[] column = new int[configHeight];
                for (int i = 0; i < tokens.length; i++) {
                    column[i] = Integer.parseInt(tokens[i]);
                }

                /* Add the integer array, representing the column, into the config vector */
                this.configuration.addElement(column);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
