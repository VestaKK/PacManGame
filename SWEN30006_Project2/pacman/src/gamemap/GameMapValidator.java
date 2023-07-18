package gamemap;

import ch.aplu.jgamegrid.Location;
import logger.EditorErrorCallback;
import matachi.mapeditor.editor.GridModelLoader;
import matachi.mapeditor.editor.Tile;
import matachi.mapeditor.editor.TileManager;
import matachi.mapeditor.grid.Grid;
import pacman.Item.GoldHandler;
import pacman.Item.IceHandler;
import pacman.Item.ItemHandler;
import pacman.Item.PillHandler;
import pacman.utility.HashLocation;

import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * GameMapValidator contains information related to how to validate and create a GameMap.
 */
public class GameMapValidator {
    private GameMap gameMap;
    private File mapFile;
    private ArrayList<Location> pillLocations;
    private ArrayList<Location> goldLocations;
    private ArrayList<Location> pacLocations;
    private ArrayList<Location> tx5StartLocations;
    private ArrayList<Location> trollStartLocations;
    private HashMap<String, ArrayList<Location>> string2Portal;
    private ItemHandler goldHandler;
    private ItemHandler iceHandler;
    private ItemHandler pillHandler;
    boolean isValidMap;
    private EditorErrorCallback editorErrorCallback;

    /**
     * Contructs a GameMapValidator using the given map file
     * @param mapFile
     * @param editorErrorCallback
     */
    public GameMapValidator(File mapFile, EditorErrorCallback editorErrorCallback) {

        this.isValidMap = false;
        this.mapFile = mapFile;
        this.editorErrorCallback = editorErrorCallback;

        Grid grid = GridModelLoader.createModel(mapFile);
        char[][] map = grid.getMap();
        int mapWidth = grid.getWidth();
        int mapHeight = grid.getHeight();

        Space[][] spaceMap = new Space[mapHeight][mapWidth];

        pacLocations = new ArrayList<>();
        pillLocations = new ArrayList<>();
        goldLocations = new ArrayList<>();
        tx5StartLocations = new ArrayList<>();
        trollStartLocations = new ArrayList<>();

        // Items
        goldHandler = new GoldHandler();
        iceHandler = new IceHandler();
        pillHandler = new PillHandler();

        /// Portals
        ArrayList<Location> whitePortalLocations = new ArrayList<>();
        ArrayList<Location> yellowPortalLocations = new ArrayList<>();
        ArrayList<Location> darkGoldPortalLocations = new ArrayList<>();
        ArrayList<Location> darkGrayPortalLocations = new ArrayList<>();

        string2Portal = new HashMap<String, ArrayList<Location>>();
        string2Portal.put("PortalWhiteTile", whitePortalLocations);
        string2Portal.put("PortalYellowTile", yellowPortalLocations);
        string2Portal.put("PortalDarkGoldTile", darkGoldPortalLocations);
        string2Portal.put("PortalDarkGrayTile", darkGrayPortalLocations);

        // Construct Space[][] map
        for (int y=0; y<mapHeight; y++) {
            for (int x=0; x<mapWidth; x++) {

                // Default settings
                SpaceOccupier spaceOccupier = null;
                Color tileColour = Color.lightGray;
                boolean isWalkable = true;

                char tileChar = map[y][x];
                Tile tile = TileManager.getInstance().getTile(tileChar);
                String tileName = tile.getTileName();

                switch(tileName) {
                    case "PacTile" -> {
                        pacLocations.add(new Location(x, y));
                    }
                    case "WallTile" -> {
                        tileColour = Color.gray;
                        isWalkable = false;
                    }
                    case "PillTile" -> {
                        spaceOccupier = pillHandler.createItem();
                        pillLocations.add(new Location(x,y));
                    }
                    case "GoldTile" -> {
                        spaceOccupier = goldHandler.createItem();
                        goldLocations.add(new Location(x,y));
                    }
                    case "IceTile" -> {
                        spaceOccupier = iceHandler.createItem();
                    }
                    case "TrollTile" -> {
                        trollStartLocations.add(new Location(x, y));
                    }
                    case "Tx5Tile" -> {
                        tx5StartLocations.add(new Location(x, y));
                    }
                    case "PortalWhiteTile", "PortalYellowTile",
                            "PortalDarkGoldTile", "PortalDarkGrayTile"-> {

                        ArrayList<Location> portalLocations = string2Portal.get(tileName);
                        portalLocations.add(new Location(x, y));

                        if (portalLocations.size() == 2) {
                            Portal portal1 = new Portal(tile.getFilePath());
                            Portal portal2 = new Portal(tile.getFilePath());

                            int p2x = portalLocations.get(1).x;
                            int p2y = portalLocations.get(1).y;

                            int p1x = portalLocations.get(0).x;
                            int p1y = portalLocations.get(0).y;

                            spaceMap[p2y][p2x] = new Space(p2x, p2y, portal2, tileColour, true);
                            portal1.link(spaceMap[p2y][p2x]);

                            spaceMap[p1y][p1x] = new Space(p1x, p1y, portal1, tileColour, true);
                            portal2.link(spaceMap[p1y][p1x]);
                            continue;
                        }
                    }
                }
                spaceMap[y][x] = new Space(x, y, spaceOccupier, tileColour, isWalkable);
            }
        }

        gameMap = new GameMap(spaceMap, mapWidth, mapHeight);
    }

    /**
     * Attempts to validate the contained GameMap
     * @return true if the map is valid else false
     */
    public boolean validateMap() {
        return isValidMap = validateMapInternal();
    }

    private boolean validateMapInternal() {

        // Check that the there is only 1 pacman start location
        Location pacStart;
        if (pacLocations.isEmpty()) {
            editorErrorCallback.errNotEnoughPacMan(mapFile);
            return false;
        } else if (pacLocations.size() > 1) {
            editorErrorCallback.errTooManyPacMan(mapFile, pacLocations);
            return false;
        } else {
            pacStart = pacLocations.get(0);
        }

        // Check that portals are linked
        boolean validPortals = true;
        Set<String> keySet = string2Portal.keySet();
        for (String keyString : keySet.stream().toList()) {
            ArrayList<Location> portalLocations = string2Portal.get(keyString);
            String portalName = keyString.substring(keyString.indexOf('l') + 1, keyString.indexOf('T'));

            if (portalLocations.size() > 0 && portalLocations.size() != 2) {
                editorErrorCallback.errPortalsNot2(mapFile, portalName, portalLocations);
                validPortals = false;
            }
        }
        if (!validPortals)
            return false;

        // Check that there are more than 2 pills
        if (pillLocations.size() + goldLocations.size() < 2) {
            editorErrorCallback.errNotEnoughPillsOrGold(mapFile);
            return false;
        }

        // Check that all pills and gold are reachable
        ArrayList<Location> reachableLocations = getReachableLocations(pacStart);
        ArrayList<Location> inaccessablePills = new ArrayList<>();
        for (Location pillLocation : pillLocations) {
            boolean found = false;
            for (Location pilllocation : reachableLocations) {
                if (pilllocation.equals(pillLocation)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                inaccessablePills.add(pillLocation);
            }
        }
        ArrayList<Location> inaccessableGold = new ArrayList<>();
        for (Location goldLocation : goldLocations) {
            boolean found = false;
            for (Location location : reachableLocations) {
                if (location.equals(goldLocation)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                inaccessableGold.add(goldLocation);
            }
        }
        boolean pillsAndGoldAreReachable = true;
        if (!inaccessableGold.isEmpty()) {
            pillsAndGoldAreReachable = false;
            editorErrorCallback.errInaccessibleGold(mapFile, inaccessableGold);
        }
        if (!inaccessablePills.isEmpty()) {
            pillsAndGoldAreReachable = false;
            editorErrorCallback.errInaccessiblePills(mapFile, inaccessablePills);
        }

        // Any other checks can placed after here
        return pillsAndGoldAreReachable;
    }

    private ArrayList<Location> getReachableLocations(Location start) {

        Queue<Location> queue = new LinkedList<Location>();
        Set<HashLocation> visitedSet = new HashSet<HashLocation>();
        ArrayList<Location> visitedList = new ArrayList<Location>();

        visitedSet.add(new HashLocation(start));
        visitedList.add(start);
        queue.add(start);

        // Iterate through locations we can visit
        while (!queue.isEmpty()) {
            Location current = queue.remove();

            // Search through adjacent locations
            for (Location neighbour : gameMap.getSpace(current).getNeighboursOnEnter()) {

                // HashLocation is needed because Location doesn't override hashCode() method
                // that HashSet<>() needs to work properly
                if (!visitedSet.contains(new HashLocation(neighbour)) && gameMap.isWalkable(neighbour)) {
                    visitedSet.add(new HashLocation(neighbour));
                    queue.add(neighbour);
                    visitedList.add(neighbour);
                }
            }
        }

        return visitedList;
    }

    public GameMap prepareGameMap() {
        if (!isValidMap) return null;

        gameMap.setPacStart(pacLocations.get(0));
        gameMap.setPillHandler(pillHandler);
        gameMap.setGoldHandler(goldHandler);
        gameMap.setIceHandler(iceHandler);
        gameMap.setTrollStartLocations(trollStartLocations);
        gameMap.setTx5StartLocations(tx5StartLocations);

        return gameMap;
    }

}
