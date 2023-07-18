// PacGrid.java
package src;

import ch.aplu.jgamegrid.*;
import src.Item.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * PacManGameGrid is responsible for keeping track of items and game grid related data e.g.
 * where certain items are located on the grid, which locations on the grid are walkable.
 * It manages the creation of items mostly through multiple ItemHandler classes, and can subscribe
 * ItemEventListeners to ItemHandlers when requested.
 * @see src.Item.ItemHandler
 * @see src.Item.Item
 */
public class PacManGameGrid extends GameGrid implements ItemEventListener
{
  private static final int WALL = 1;
  private static final int NULL = 2;
  private static final int PILL = 3;
  private static final int GOLD = 4;
  private static final int ICE = 5;

  // Default maze
  private static final int[][] maze = {
          {WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL},
          {WALL,PILL,PILL,PILL,PILL,WALL,PILL,PILL,PILL,PILL,GOLD,PILL,PILL,PILL,WALL,PILL,PILL,PILL,PILL,WALL},
          {WALL,GOLD,WALL,WALL,PILL,WALL,PILL,WALL,WALL,WALL,WALL,WALL,WALL,PILL,WALL,PILL,WALL,WALL,PILL,WALL},
          {WALL,PILL,WALL,PILL,PILL,PILL,PILL,PILL,PILL,PILL,ICE ,PILL,GOLD,PILL,PILL,PILL,PILL,WALL,PILL,WALL},
          {WALL,PILL,WALL,PILL,WALL,WALL,PILL,WALL,WALL,NULL,NULL,WALL,WALL,PILL,WALL,WALL,PILL,WALL,PILL,WALL},
          {WALL,PILL,PILL,PILL,PILL,PILL,PILL,WALL,NULL,NULL,NULL,NULL,WALL,PILL,PILL,PILL,PILL,PILL,PILL,WALL},
          {WALL,PILL,WALL,PILL,WALL,WALL,PILL,WALL,WALL,WALL,WALL,WALL,WALL,PILL,WALL,WALL,PILL,WALL,PILL,WALL},
          {WALL,PILL,WALL,PILL,PILL,PILL,PILL,PILL,PILL,GOLD,ICE ,PILL,PILL,PILL,PILL,PILL,PILL,WALL,PILL,WALL},
          {WALL,ICE ,WALL,WALL,PILL,WALL,PILL,WALL,WALL,WALL,WALL,WALL,WALL,PILL,WALL,PILL,WALL,WALL,PILL,WALL},
          {WALL,PILL,PILL,PILL,GOLD,WALL,PILL,PILL,PILL,PILL,GOLD,PILL,PILL,PILL,WALL,PILL,PILL,PILL,PILL,WALL},
          {WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL,WALL}
  };
  private static final int NB_VERTICAL_CELLS = maze.length;
  private static final int NB_HORIZONTAL_CELLS = maze[0].length;
  private static final int CELL_SIZE = 40;
  private final boolean[][] mazeWalkable;
  private Item[][] mazeItems;
  private PillHandler pillHandler;
  private GoldHandler goldHandler;
  private IceHandler iceHandler;

  /**
   * Constructs a PacManGameGrid, ready to be set up using setUpGrid().
   */
  public PacManGameGrid()
  {
    // Initialise GameGrid state
    super(NB_HORIZONTAL_CELLS, NB_VERTICAL_CELLS, CELL_SIZE, false);

    // Initialise PacManGameGrid state
    mazeWalkable = new boolean[NB_VERTICAL_CELLS][NB_HORIZONTAL_CELLS];
    mazeItems = new Item[NB_VERTICAL_CELLS][NB_HORIZONTAL_CELLS];
    pillHandler = new PillHandler();
    goldHandler = new GoldHandler();
    iceHandler = new IceHandler();

    // Subscribe the gameGrid to its itemHandlers
    pillHandler.subscribe(this);
    goldHandler.subscribe(this);
    iceHandler.subscribe(this);

    // Initialise bitmap of walkable locations
    for (int y=0; y<NB_VERTICAL_CELLS; y++) {
      for (int x=0; x<NB_HORIZONTAL_CELLS; x++) {
        int block = maze[y][x];
        mazeWalkable[y][x] = block == WALL ? false : true;
      }
    }

  }

  /**
   * Internal default function for setting up the PacManGameGrid when provided no
   * pill or gold locations.
   */
  private void setUpGrid() {

    // Iterate through default maze to and add items accordingly
    for (int y=0; y<NB_VERTICAL_CELLS; y++) {
      for (int x=0; x<NB_HORIZONTAL_CELLS; x++) {
        int block = maze[y][x];

        if (block == WALL)
          continue;

        Location location = new Location(x,y);

        if (block == PILL) {
          putItem(pillHandler.createItem(), location);
        } else if (block == GOLD) {
          putItem(goldHandler.createItem(), location);
        } else if (block == ICE) {
          putItem(iceHandler.createItem(), location);
        } else {
          putItem(null, location);
        }
      }
    }

  }

  /**
   * Sets up PacManGameGrid using a list of pill and gold locations, adding items to the
   * GameGrid if they lie in the gameGrid's dimensions.
   * @param propertyPillLocations list of pill locations (Can be empty)
   * @param propertyGoldLocations list of gold locations (Can be empty)
   */
  public void setUpGrid(
          ArrayList<Location> propertyPillLocations,
          ArrayList<Location> propertyGoldLocations) {

    // If the lists are empty, set the grid up using
    if (propertyGoldLocations.size() == 0 && propertyPillLocations.size() == 0) {
      setUpGrid();
      return;
    }

    // Add pills to the game grid
    for (Location location : propertyPillLocations) {
      if (isInGrid(location)) {
        putItem(pillHandler.createItem(), location);
      }
    }

    // Add gold to the game grid
    for (Location location : propertyGoldLocations) {
      if (isInGrid(location)) {
        putItem(goldHandler.createItem(), location);
      }
    }

    // Add ice to the game grid
    for (int y=0; y<NB_VERTICAL_CELLS; y++) {
      for (int x=0; x<NB_HORIZONTAL_CELLS; x++) {
        int block = maze[y][x];

        if (block == ICE) {
          putItem(iceHandler.createItem(), new Location(x,y));
        }
      }
    }

  }

  /**
   * Adds item to the gameGrid and mazeItems[], given
   * they are in the bounds of the game grid.
   * @param item item created using an ItemHandler
   * @param location location within the game grid
   */
  private void putItem(Item item, Location location) {
    if (!setItem(item, location) || item == null)
      return;

    addActor(item, location);
  }


  /**
   * Adds item to mazeItems if they are in the bounds
   * of the game grid.
   * @param item item created using an ItemHandler
   * @param location location within the game grid
   * @return true if the item was placed in mazeItems[], else false
   */
  private boolean setItem(Item item, Location location) {
    if (this.isInGrid(location)) {
      mazeItems[location.y][location.x] = item;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks if the provided location can be walked on.
   * @param location location in the game grid
   * @return true if the location is walkable, else false
   */
  public Boolean isWalkable(Location location) {
    if (this.isInGrid(location))
      return mazeWalkable[location.y][location.x];
    else
      return false;
  }

  /**
   * Renders the maze background in color.
   */
  private void colorMaze() {
    GGBackground bg = getBg();
    bg.clear(Color.gray);

    for (int y=0; y<NB_VERTICAL_CELLS; y++) {
      for (int x=0; x<NB_HORIZONTAL_CELLS; x++) {
        Location location = new Location(x,y);
        if(isWalkable(location)) {
          bg.fillCell(location, Color.lightGray);
        }
      }
    }

  }

  /**
   * Renders the item provided.
   * @param item item created by ItemHandler
   */
  private void drawItem(Item item) {
    GGBackground bg = getBg();
    bg.setPaintColor(item.getColor());
    bg.fillCircle(toPoint(item.getLocation()), item.getItemRadius());
    item.show();
  }

  /**
   * Renders the game grid background, as well as all
   * items on the grid
   */
  public void drawGrid() {
    colorMaze();
    for (int y=0; y<NB_VERTICAL_CELLS; y++) {
      for (int x=0; x<NB_HORIZONTAL_CELLS; x++) {
        Location location = new Location(x,y);
        Item item = getItemAt(location);
        if (item != null)
          drawItem(item);
      }
    }

  }

  /**
   * When an item is consumed, the game grid removes the item from mazeItems[].
   * @see ItemEventListener
   */
  @Override
  public void onItemConsumed(ItemEventCode code, Location location) {
    if(setItem(null, location))
      getBg().fillCell(location, Color.lightGray);
  }

  /**
   * Returns Item from the given location.
   * @param location Location in the game grid
   * @return Item at location provided (Can be null)
   */
  public Item getItemAt(Location location) {
    if (this.isInGrid(location))
      return mazeItems[location.y][location.x];
    else
      return null;
  }
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
}

