package logger;

import ch.aplu.jgamegrid.Location;
import java.io.File;
import java.util.ArrayList;

/**
 * EditorErrorCallback writes various different errors into a single editor-error log file
 */
public class EditorErrorCallback extends LogWriter {
    private static final String logFilePath = "editorErrorLog.txt";
    public EditorErrorCallback() {
        super(logFilePath);
    }
    public void errLevelToManyFiles(File gameFolder, ArrayList<File> fileList) {
        String string  = String.format("%s - multiple maps at the same level: ", gameFolder.getName());

        for (int i=0; i<fileList.size(); i++) {
            File file = fileList.get(i);

            if (i == fileList.size() - 1) {
                string += file.getName();
            } else {
                string += String.format("%s; ", file.getName());
            }
        }

        writeString(string);
    }

    public void errNoValidFiles(File gameFolder) {
        String string  = String.format("%s - no maps found", gameFolder.getName());
        writeString(string);
    }

    public void errInaccessiblePills(File file, ArrayList<Location> pillList) {
        String string = String.format("Level %s - Pill not accessible: ", file.getName());
        string += listOfLocations(pillList);
        writeString(string);
    }

    public void errInaccessibleGold(File file, ArrayList<Location> goldList) {
        String string = String.format("Level %s - Gold not accessible: ", file.getName());
        string += listOfLocations(goldList);
        writeString(string);
    }

    public void errNotEnoughPillsOrGold(File file) {
        String string = String.format("Level %s - less than 2 Gold or Pill", file.getName());
        writeString(string);
    }

    public void errPortalsNot2(File file, String portalName, ArrayList<Location> portalLocations) {
        String string = String.format("Level %s - portal %s count is not 2: ", file.getName(), portalName);
        string += listOfLocations(portalLocations);
        writeString(string);
    }

    public void errNotEnoughPacMan(File file) {
        String string = String.format("Level %s - no start for PacMan", file.getName());
        writeString(string);
    }

    public void errTooManyPacMan(File file, ArrayList<Location> pacLocations) {
        String string = String.format("Level %s - more than one start for Pacman", file.getName());
        string += listOfLocations(pacLocations);
        writeString(string);
    }


    public String listOfLocations(ArrayList<Location> locations) {
        String string = "";
        for (int i=0; i<locations.size(); i++) {
            Location location = locations.get(i);

            if (i == locations.size() - 1) {
                string += String.format("(%d,%d)", location.x + 1, location.y + 1);
            } else {
                string += String.format("(%d,%d); ", location.x + 1, location.y + 1);
            }
        }
        return string;
    }

}
