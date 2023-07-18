package src.Item;

import java.awt.*;

public class Pill extends Item {
    private static final int PILL_RADIUS = 5;
    private static final Color COLOR = Color.white;

    /**
     * Constructor for a Pill. Requires a PillHandler
     * @param pillHandler pillHandler that will manage this pill
     */
    protected Pill(PillHandler pillHandler) { super(COLOR, PILL_RADIUS, pillHandler); }
    @Override
    public void consume() {
        itemHandler.notifySubs(ItemEventCode.IEC_PILL_CONSUMED, this.getLocation());
        this.consumeSelf();
    }
}
