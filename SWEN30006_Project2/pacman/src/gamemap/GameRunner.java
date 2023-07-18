package gamemap;

import logger.GameCallback;
import logger.EditorErrorCallback;
import pacman.Game;
import pacman.utility.PropertiesLoader;

import java.io.File;
import java.util.*;

/**
 * GameRunner takes a folder of games and has the ability to validate and test the folder of games.
 * It prints any errors using the provided EditerErrorCallback
 */
public class GameRunner {
    private File gameFolder;
    private ArrayList<File> gameFiles;
    private File currentMapFile;
    private EditorErrorCallback editorErrorCallback;
    private String propertiesString;
    private boolean isValidGame;

    /**
     * Creates a gameRunner to test the gameFolder provided. In order to test the game,
     * the application must call gameRunner.validateGame() before gameRunner.testGame()
     * @param gameFolder
     * @param editorErrorCallback
     * @param propertiesString
     */
    public GameRunner(File gameFolder, EditorErrorCallback editorErrorCallback, String propertiesString) {
        this.gameFolder = gameFolder;
        this.gameFiles = new ArrayList<File>();
        this.isValidGame = false;
        this.editorErrorCallback = editorErrorCallback;
        this.propertiesString = propertiesString;
    }

    /**
     * Vaidates the Game Folder
     * @return if the game is valid, this returns true. THis means that testGame() will attempt to play
     * all the map files that were provided to this GameRunner. Otherwise, this returns null andx testGame()
     * will fail.
     */
    public boolean validateGame() {
        return isValidGame = validateGameInternal();
    }

    /**
     * Tests the game.
     * @return If validateGame() hasn't been called beforehand, this will return false. Otherwise, this function
     * will return whether the game was tested without error (true if there were no errors in testing the game, false
     * if errors were found in one of the maps). If false, getCurrentFile() will return the file that
     * the GameRunner found an error on.
     */
    public boolean testGame() {
        if (!isValidGame) return false;

        for (File file : gameFiles) {
            this.currentMapFile = file;
            GameMapValidator gameMapValidator = new GameMapValidator(file, editorErrorCallback);

            if (!gameMapValidator.validateMap()) {
                return false;
            }

            Properties properties = PropertiesLoader.loadPropertiesFile(propertiesString);
            GameCallback gameCallback = new GameCallback();
            GameMap gameMap = gameMapValidator.prepareGameMap();
            Game game = new Game(gameCallback, properties, gameMap);

            boolean pacManWon = game.run();
            game.close();

            if (!pacManWon)
                return true;

        }

        return true;
    }

    /**
     * Internal function for validateGame()
     * @return
     */
    private boolean validateGameInternal() {
        File[] mapFiles = gameFolder.listFiles();

        if (mapFiles == null) {
            editorErrorCallback.errNoValidFiles(gameFolder);
            return false;
        }

        // Check that valid files exist
        ArrayList<File> validFiles = new ArrayList<File>();
        for (File file : mapFiles) {
            // Check that the file is a file
            if (!file.isFile())
                continue;

            String fileName = file.getName();

            // Check that the file is a xml file
            if (!fileName.substring(fileName.indexOf(".") + 1).equals("xml"))
                continue;

            // Check that the file name starts with a digit at least
            if (Character.isDigit(fileName.charAt(0))) {
                validFiles.add(file);
            }
        }

        // If we have no valid files, return false
        if (validFiles.isEmpty()) {
            editorErrorCallback.errNoValidFiles(gameFolder);
            return false;
        }

        // Check that each file is linked to a unique integer
        HashMap<Integer, ArrayList<File>> int2FileList = new HashMap<Integer, ArrayList<File>>();

        for (File file : validFiles) {
            String fileName = file.getName();
            String num_str = "";

            for (char character : fileName.toCharArray()) {
                if(!Character.isDigit(character)) break;
                num_str += character;
            }

            Integer fileNum = Integer.parseInt(num_str);
            ArrayList<File> files = int2FileList.get(fileNum);

            if (files == null) {
                ArrayList<File> newFileList = new ArrayList<File>();
                newFileList.add(file);
                int2FileList.put(fileNum, newFileList);
            } else {
                files.add(file);
            }
        }

        Set<Integer> keySet = int2FileList.keySet();

        // Check that the game folder has only one number linked to
        // a single file
        boolean isValidGameFolder = true;
        for (Integer intKey : keySet.stream().toList()) {
            ArrayList<File> fileList = int2FileList.get(intKey);

            if (fileList.size() > 1) {
                isValidGameFolder = false;
                editorErrorCallback.errLevelToManyFiles(gameFolder, fileList);
            }
        }

        if (isValidGameFolder) {
            int[] intKeys = keySet.stream().mapToInt(i->i).toArray();
            Arrays.sort(intKeys);

            for (int key : intKeys) {
                File gamefile = int2FileList.get(key).get(0);
                gameFiles.add(gamefile);
            }
        }

        return isValidGameFolder;
    }

    public File getCurrentMapFile() { return currentMapFile; }
}
