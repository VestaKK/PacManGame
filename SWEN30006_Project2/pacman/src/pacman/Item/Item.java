package pacman.Item;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import gamemap.Space;
import gamemap.SpaceOccupier;
import pacman.Entity.Entity;
import pacman.Entity.PacActor;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Items require an ItemHandler to be created and distributed in the codebase. Items will use ItemHandlers
 * to notify relevant ItemEventListeners of their consumption. Classes that inherit from Item should
 * override consume() to suit the functionality of their item. A default implementation is provided.
 */
public abstract class Item extends Actor implements SpaceOccupier {
    private final Color color;
    private final int radius;
    protected final ItemHandler itemHandler;
    private boolean isConsumed = false;

    /**
     * Constructor for an Item with an image.
     * @param imgFileName filename to an image
     * @param color item color
     * @param itemHandler itemHandler that will manage this item and notify its subscribers when this item
     *                    is consumed
     */
    protected Item(String imgFileName, Color color, ItemHandler itemHandler) {
        super(imgFileName);
        this.color = color;
        this.itemHandler = itemHandler;
        this.radius = 0;
    }

    /**
     * Constructor for an Item without an image.
     * @param color item color
     * @param radius radius of item
     * @param itemHandler itemHandler that will manage this item and notify its subscribers when this item
     *                    is consumed
     */
    protected Item(Color color, int radius, ItemHandler itemHandler) {
        super();
        this.color = color;
        this.itemHandler = itemHandler;
        this.radius = radius;
    }

    /**
     * Consumes item and notifies item handler of its consumption. Subclasses should override this
     * function
     */
    public abstract void consume();
    protected void consumeSelf() {
        this.gameGrid.getBg().fillCell(getLocation(), Color.lightGray);
        this.hide();
        this.isConsumed = true;
    }
    public void interact(Entity e) {
        if (e.getClass() == PacActor.class) {
            if (!this.isConsumed) {
                this.consume();
            }
        }
    }
    public void placeOnto(GameGrid gameGrid, Space space) {
        gameGrid.addActor(this, space.getLocation());
        GGBackground bg = gameGrid.getBg();
        bg.setPaintColor(this.getColor());
        bg.fillCircle(gameGrid.toPoint(this.getLocation()), this.getItemRadius());
        this.show();
    }
    public ArrayList<Location> neighboursOnEnter(Space space) {
        return null;
    }
    public ArrayList<Location> neighboursOnExit(Space space) {
        return null;
    }
    public Color getColor() { return this.color; }
    public int getItemRadius() { return radius; }
    public boolean isConsumed() { return isConsumed; }
}
