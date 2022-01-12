package TileMap;

import Main.GamePanel;
import Networking.WeatherData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

/**
 * A tile configuration is a pre-loaded chunk of tiles that can be dynamically appended to a level's TileMap. The
 * elements of this enum all return Vectors of int arrays (of minimum length 20) via the getConfiguration() method.
 * The configurations are loaded from files inside the Resources/Maps directory. The .map configuration files can be
 * thought of as transposed matrices containing the integer tile IDs of the configuration. To get a feeling for how
 * they'd look as a map chunk, swap the rows and columns.
 */
public enum TileConfiguration {
    DEFAULT("Resources/Maps/tileConfigDefault.map", true),
    LEDGE("Resources/Maps/tileConfigLedge.map", false),
    LEDGE2("Resources/Maps/tileConfigLedge2.map", false),
    STAIRS1("Resources/Maps/tileConfigStairs1.map", false),
    STAIRS2("Resources/Maps/tileConfigStairs2.map", false),
    BUMPS1("Resources/Maps/tileConfigBumps1.map", false),
    BUMPS2("Resources/Maps/tileConfigBumps2.map", false),
    PITS1("Resources/Maps/tileConfigPits1.map", false),
    PITS2("Resources/Maps/tileConfigPits2.map", false),
    PITS3("Resources/Maps/tileConfigPits3.map", false),
    PLATFORMS1("Resources/Maps/tileConfigPlatforms1.map", false),
    PLATFORMS2("Resources/Maps/tileConfigPlatforms2.map", false),
    HOLE1("Resources/Maps/tileConfigHole1.map", false),
    HOLE2("Resources/Maps/tileConfigHole2.map", false),
    HOLE3("Resources/Maps/tileConfigHole3.map", false);

    private final int configLength;
    private final int configHeight;
    private final Vector<int[]> configuration;
    private final boolean extendable;

    public Vector<int[]> getConfiguration() {
        return this.configuration;
    }

    public boolean getExtendable() {
        return this.extendable;
    }

    public int getLength() {
        return this.configLength;
    }

    public static TileConfiguration[] getRainConfigs() {
        return new TileConfiguration[] { PITS1, PITS2, PITS3, PLATFORMS1, PLATFORMS2, HOLE1, HOLE2, HOLE3};
    }

    public static TileConfiguration[] getClearConfigs() {
        return new TileConfiguration[] { LEDGE, LEDGE2, BUMPS1, BUMPS2, STAIRS1, STAIRS2 };
    }

    public static TileConfiguration[] getCloudConfigs() {
        return new TileConfiguration[] { PITS1, PITS2, PITS3, PLATFORMS1, PLATFORMS2, HOLE1, HOLE2, HOLE3, LEDGE, LEDGE2, BUMPS1, BUMPS2, STAIRS1, STAIRS2 };
    }

    /**
     * Enum constructor that reads the specified map config file and converts it into a vector.
     */
    TileConfiguration(String mapFileLocator, boolean extendable) {
        configHeight = GamePanel.HEIGHT / GamePanel.TILESIZE;

        configuration = new Vector<>();

        loadMapFile(mapFileLocator, configuration);
        this.configLength = this.configuration.size();
        this.extendable = extendable;
    }

    private void loadMapFile(String s, Vector<int[]> config) {
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
                config.addElement(column);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TileConfiguration[] getAvailableConfigsForWeather(WeatherData weatherData) {
        String weather = weatherData.getWeatherString();

        switch (weather) {
            case "Clear":
                return getClearConfigs();
            case "Clouds":
                return getCloudConfigs();
            case "Thunderstorm":
            case "Drizzle":
            case "Rain":
            case "Snow":
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Dust":
            case "Fog":
            case "Sand":
            case "Ash":
            case "Squall":
            case "Tornado":
            default:
                return getRainConfigs();
        }
    }
}
