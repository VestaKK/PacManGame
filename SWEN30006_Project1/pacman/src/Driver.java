package src;

import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test1.properties";

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        GameCallback gameCallback = new GameCallback();

        // Can't set up game without properties
        assert properties != null: "Properties not found!";

        // Create game and run it
        Game game = new Game(gameCallback, properties);
        game.run();
    }
}
