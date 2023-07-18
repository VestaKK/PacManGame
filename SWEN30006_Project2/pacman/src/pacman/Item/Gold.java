package pacman.Item;

import ch.aplu.jgamegrid.GameGrid;
import pacman.Entity.Entity;
import pacman.Entity.PacActor;

import java.awt.*;
import gamemap.Space;
public class Gold extends Item{
    private static final String IMG_FILE_NAME = "pacman/sprites/gold.png";
    private static final Color COLOR = Color.yellow;

    /**
     * Constructor for Gold. Requires a GoldHandler
     * @param goldHandler goldHandler that will manage this gold
     */
    protected Gold(GoldHandler goldHandler) {
        super(IMG_FILE_NAME, COLOR, goldHandler);
    }
    @Override
    public void consume() {
        itemHandler.notifySubs(ItemEventCode.IEC_GOLD_CONSUMED, this.getLocation());
        this.consumeSelf();
    }
}
