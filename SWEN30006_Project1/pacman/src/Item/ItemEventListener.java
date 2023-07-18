package src.Item;

import ch.aplu.jgamegrid.Location;

public interface ItemEventListener {

    /**
     * ItemEventListeners respond to item.consume() based on the ItemEventCode emitted from that Item
     * and it's ItemHandler
     * @param code Code emitted from Item and propogated by ItemHandler
     * @param location location where item was consumed
     */
    void onItemConsumed(ItemEventCode code, Location location);
}
