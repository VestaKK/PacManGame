package src.Item;

public class IceHandler extends ItemHandler {
    public IceHandler() {super();}
    @Override
    protected Item generateItem() {
        Item newIce = new Ice(this);
        return newIce;
    }
}
