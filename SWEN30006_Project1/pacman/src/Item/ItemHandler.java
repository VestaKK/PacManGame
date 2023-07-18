package src.Item;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;

/**
 * ItemHandler manages the creation and distribution of Item. An ItemHandler maintains a list of
 * items it has created, as well as a list of ItemEventListeners that will respond to an item being
 * consumed. Classes that inherit ItemHandler must implement generateItem().
 */
public abstract class ItemHandler {
    protected ArrayList<Item> itemArray;
    protected ArrayList<ItemEventListener> subscriberArray;

    /**
     * Constructor of an ItemHandler
     */
    public ItemHandler() {
        itemArray = new ArrayList<Item>();
        subscriberArray = new ArrayList<ItemEventListener>();
    }
    protected abstract Item generateItem();

    /**
     * Creates item managed by this itemHandler
     * @return item
     */
    public Item createItem() {
        Item item = generateItem();
        this.addItem(item);
        return item;
    }

    /**
     * Propagates provided ItemEventCode to this ItemHandler's list of subscribes
     * @param code
     * @param location
     */
    protected void notifySubs(ItemEventCode code, Location location) {
        for (ItemEventListener e: subscriberArray) {
            e.onItemConsumed(code, location);
        }
    }
    private void addItem(Item item) { itemArray.add(item); }
    public void subscribe(ItemEventListener listener) { subscriberArray.add(listener); }
    public ArrayList<Item> getItems() { return new ArrayList<Item>(this.itemArray); }


}