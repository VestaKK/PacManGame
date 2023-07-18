package gamemap;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import pacman.Entity.Entity;

import java.awt.*;
import java.util.ArrayList;

/**
 * Space will attempt to interact with any Entity that lands on them. It is only aware of its location
 * and several neighbour locations, as well as how the space should be coloured.
 */
public class Space {
    private final Location location;
    private final boolean isWalkable;
    private final SpaceOccupier spaceOccupier;
    private ArrayList<Location> neighbours;
    private final Color tileColour;
    public Space(int x, int y, SpaceOccupier spaceOccupier, Color tileColour, boolean isWalkable) {
        this.location = new Location(x,y);
        this.spaceOccupier = spaceOccupier;
        this.isWalkable = isWalkable;
        this.tileColour = tileColour;

        // Get total neighbours, neighbours into the space and
        // neighbours out of the space
        this.neighbours = new ArrayList<Location>();

        // Calculate neighbour space locations
        Location.CompassDirection[] compassDirections = {
                Location.CompassDirection.NORTH,
                Location.CompassDirection.EAST,
                Location.CompassDirection.SOUTH,
                Location.CompassDirection.WEST
        };

        for (Location.CompassDirection direction : compassDirections) {
            this.neighbours.add(this.location.getNeighbourLocation(direction));
        }
    }

    public Location getLocation() { return location.clone(); }

    public ArrayList<Location> getNeighbours() { return new ArrayList<>(neighbours); }
    public ArrayList<Location> getNeighboursOnEnter() {
        if (spaceOccupier == null) return getNeighbours();
        ArrayList<Location> neighboursOnEnter = spaceOccupier.neighboursOnEnter(this);
        return neighboursOnEnter == null ? neighbours : neighboursOnEnter;
    }
    public ArrayList<Location> getNeighboursOnExit() {
        if (spaceOccupier == null) return getNeighbours();
        ArrayList<Location> neighboursOnExit = spaceOccupier.neighboursOnExit(this);
        return neighboursOnExit == null ? neighbours : neighboursOnExit;
    }

    public void placeOnto(GameGrid gameGrid) {


        gameGrid.getBg().fillCell(location, tileColour);
        if (spaceOccupier != null) {
            spaceOccupier.placeOnto(gameGrid, this);
        }
    }

    public void landedOnBy(Entity e) {
        if (spaceOccupier != null) {
            spaceOccupier.interact(e);
        }
    }

    public boolean isWalkable() { return this.isWalkable; }
}
