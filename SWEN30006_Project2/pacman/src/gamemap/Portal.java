package gamemap;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import pacman.Entity.Entity;

import java.util.ArrayList;

/**
 * Portal allows for anyone who steps on it to teleport to a lined portal
 */
public class Portal extends Actor implements SpaceOccupier {
    private Space other;
    public Portal(String filePath) {
        super(filePath);
    }
    public void link(Space otherPortalSpace) {
        this.other = otherPortalSpace;
    }
    @Override
    public void interact(Entity e) {
        e.setLocation(other.getLocation());
    }
    public void placeOnto(GameGrid gameGrid, Space space) {
        gameGrid.addActor(this, space.getLocation());
        this.show();
    }

    // Since entering a portal teleports you instantly to the other portal,
    // The effective neighbours of the portal is the neighbours of the other portal
    public ArrayList<Location> neighboursOnEnter(Space space) {
        if (other == null) return null;
        return other.getNeighbours();
    }

    // Exiting a portal is the same as a normal pathTile
    public ArrayList<Location> neighboursOnExit(Space space) {
        return null;
    }
}
