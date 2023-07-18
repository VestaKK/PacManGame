package src.Item;

public class GoldHandler extends ItemHandler{
    public GoldHandler() { super(); }
    @Override
    protected Item generateItem() {
        Item newGold = new Gold(this);
        return newGold;
    }
}
