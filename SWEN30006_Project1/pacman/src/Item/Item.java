package src.Item;

import ch.aplu.jgamegrid.Actor;

import java.awt.Color;

/**
 * Items require an ItemHandler to be created and distributed in the codebase. Items will use ItemHandlers
 * to notify relevant ItemEventListeners of their consumption. Classes that inherit from Item should
 * override consume() to suit the functionality of their item. A default implementation is provided.
 */
public abstract class Item extends Actor {
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
    public void consume() {
        itemHandler.notifySubs(ItemEventCode.IEC_CODE_DEFAULT, this.getLocation());
        consumeSelf();
    }

    protected void consumeSelf() {
        this.hide();
        this.isConsumed = true;
    }
    public Color getColor() { return this.color; }
    public int getItemRadius() { return radius; }
    public boolean isConsumed() { return isConsumed; }
}
