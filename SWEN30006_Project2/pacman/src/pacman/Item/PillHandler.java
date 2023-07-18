package pacman.Item;

public class PillHandler extends ItemHandler {
    public PillHandler() {
        super();
    }
    @Override
    protected Item generateItem() {
        Item newPill = new Pill(this);
        return newPill;
    }
}
