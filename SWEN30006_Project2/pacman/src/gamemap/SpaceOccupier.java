package gamemap;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import pacman.Entity.*;

import java.util.ArrayList;

/**
 * Interface for anything that would want to occupy a space.
 * A can interact with entities that step on them, and they can
 * also modify the potential options that an Entity has to move
 * when they both enter or exit a space.
 */
public interface SpaceOccupier {
    void interact(Entity e);
    void placeOnto(GameGrid gameGrid, Space space);
    ArrayList<Location> neighboursOnEnter(Space space);
    ArrayList<Location> neighboursOnExit(Space space);
}