package pacman.Item;

import ch.aplu.jgamegrid.GameGrid;
import gamemap.Space;
import pacman.Entity.Entity;
import pacman.Entity.PacActor;

import java.awt.*;

public class Ice extends Item{
    private static final Color COLOR = Color.blue;
    private static final String IMG_FILE_NAME = "pacman/sprites/ice.png";

    /**
     * Constructor for Ice. Requires an IceHandler.
     * @param iceHandler iceHandler that will manage this ice
     */
    protected Ice(IceHandler iceHandler) {
        super(IMG_FILE_NAME, COLOR, iceHandler);
    }
    @Override
    public void consume() {
        itemHandler.notifySubs(ItemEventCode.IEC_ICE_CONSUMED, this.getLocation());
        this.consumeSelf();
    }
}
