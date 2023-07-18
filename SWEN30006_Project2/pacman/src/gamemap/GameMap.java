package gamemap;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import pacman.Entity.Entity;
import pacman.Item.*;

import java.util.ArrayList;

/**
 * Game Map contains the necessary information so that a game can be ran. Can only be created by
 * a GameMapValidator.
 */
public class GameMap {
    private static final int CELL_SIZE = 40;
    private Space spaceMap[][];
    private int mapWidth;
    private int mapHeight;
    private ItemHandler goldHandler;
    private ItemHandler iceHandler;
    private ItemHandler pillHandler;
    private Location pacStart;
    private ArrayList<Location> tx5StartLocations;
    private ArrayList<Location> trollStartLocations;

    /**
     * A gameMap that is prepared by a gameMapValidator is assumed to be valid
     * @param spaceMap
     * @param mapWidth
     * @param mapHeight
     */
    protected GameMap(Space[][] spaceMap, int mapWidth, int mapHeight) {

        this.spaceMap = spaceMap;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        // Some start Locations
        tx5StartLocations = new ArrayList<>();
        trollStartLocations = new ArrayList<>();

        // Items
        goldHandler = new GoldHandler();
        iceHandler = new IceHandler();
        pillHandler = new PillHandler();
    }

    // These are used by GameMapTester;
    protected void setPacStart(Location location) { pacStart = location; }
    protected void setPillHandler(ItemHandler pillHandler) { this.pillHandler = pillHandler; }
    protected void setGoldHandler(ItemHandler goldHandler) { this.goldHandler = goldHandler; }
    protected void setIceHandler(ItemHandler iceHandler) { this.iceHandler = iceHandler; }
    protected void setTrollStartLocations(ArrayList<Location> trollStartLocations) { this.trollStartLocations = new ArrayList<>(trollStartLocations); }
    protected void setTx5StartLocations(ArrayList<Location> tx5StartLocations) { this.tx5StartLocations = new ArrayList<>(tx5StartLocations); }

    /**
     * Creates a game grid with the same dimensions as the GameMap
     * @return
     */
    public GameGrid createGameGrid() {

        // Take every space and attach each one to the relevant grid location in gameGrid.
        GameGrid output = new GameGrid(mapWidth, mapHeight, CELL_SIZE);
        for (int y=0; y<mapHeight; y++) {
            for (int x=0; x<mapWidth; x++) {
                Space space = spaceMap[y][x];
                space.placeOnto(output);
            }
        }
        return output;
    }

    public Space getSpace(Location location) {
        return spaceMap[location.y][location.x];
    }
    public boolean isWalkable(Location location) {
        if (location.x >= 0 && location.x < mapWidth &&
            location.y >= 0 && location.y < mapHeight) {
            return getSpace(location).isWalkable();
        }
        return false;
    }
    public Location getPacStart() { return pacStart.clone(); }
    public ArrayList<Location> getTx5StartLocations() { return new ArrayList<>(tx5StartLocations); }
    public ArrayList<Location> getTrollStartLocations() { return new ArrayList<>(trollStartLocations); }
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public ArrayList<Item> getPills() { return pillHandler.getItems(); }
    public ArrayList<Item> getGold() { return goldHandler.getItems(); }
    public ArrayList<Item> getIce() { return iceHandler.getItems(); }

    /**
     * Retrieves all items from the game grid and returns them in a single
     * ArrayList.
     * @return ArrayList of items
     */
    public ArrayList<Item> getAllItems() {
        ArrayList<Item> allItems = new ArrayList<Item>();
        allItems.addAll(pillHandler.getItems());
        allItems.addAll(goldHandler.getItems());
        allItems.addAll(iceHandler.getItems());
        return allItems;
    }

    /**
     * Subscribes an ItemEventListener to the PillHandler
     * @param listener ItemEventListener that responds to pill.consume()
     */
    public void subscribeToPillHandler(ItemEventListener listener) {
        pillHandler.subscribe(listener);
    }

    /**
     * Subscribes an ItemEventListener to the GoldHandler
     * @param listener ItemEventListener that responds to gold.consume()
     */
    public void subscribeToGoldHandler(ItemEventListener listener) {
        goldHandler.subscribe(listener);
    }

    /**
     * Subscribes an ItemEventListener to the IceHandler
     * @param listener ItemEventListener that responds to ice.consume()
     */
    public void subscribeToIceHandler(ItemEventListener listener) {
        iceHandler.subscribe(listener);
    }

    public void moveTo(Location location, Entity e) {
        e.setLocation(location);
        getSpace(location).landedOnBy(e);
    }
}
