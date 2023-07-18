package pathfinder;

import ch.aplu.jgamegrid.Location;
import gamemap.GameMap;
import pacman.Entity.Monster;
import pacman.Entity.PacActor;
import pacman.Item.Item;
import pacman.utility.HashLocation;

import java.util.*;

/**
 * AutoPlayer is a class that can calculate Locations to move to based on the given
 * GameMap. You can provide the AutoPlayer with some objects to avoid.
 */
public class AutoPlayer {
    private GameMap gameMap;
    private ArrayList<PathObject> avoidedObjects;
    public AutoPlayer(GameMap gameMap) {
        this.gameMap = gameMap;
        this.avoidedObjects = new ArrayList<>();
    }
    public Location findNext(Location start, Location end) {

        // State used by algorithm
        Queue<Location> queue = new LinkedList<Location>();
        Set<HashLocation> visitedSet = new HashSet<HashLocation>();
        Map<Location, Location> parentMap = new HashMap<Location, Location>();

        // Map the start to itself, so we don't get null results
        parentMap.put(start, start);

        // Add neighbours to the queue
        // Neighbours on Exit is important -> there is a difference between
        // entering and exiting a portal i.e. a portal entered effectively has
        // the neighbours of it's paired portal, but exiting a portal acts
        // like a regular path tile.
        for (Location neighbour : gameMap.getSpace(start).getNeighboursOnExit()) {

            if (shouldAvoid(neighbour))
                continue;

            if (gameMap.isWalkable(neighbour)) {
                parentMap.put(neighbour, start);
                visitedSet.add(new HashLocation(neighbour));
                queue.add(neighbour);
            }
        }

        // Iterate through locations we can visit
        while (!queue.isEmpty()) {
            Location current = queue.remove();

            // Search for the end location
            if (current.equals(end)) {

                // Reconstruct the path back to the start
                Location next = current;
                while (parentMap.get(next) != start) {
                    next = parentMap.get(next);
                }
                return next;
            }

            for (Location neighbour : gameMap.getSpace(current).getNeighboursOnEnter()) {

                if (shouldAvoid(neighbour))
                    continue;

                // HashLocation is needed because Location doesn't override hashCode() method
                // that HashSet<>() needs to work properly
                if (!visitedSet.contains(new HashLocation(neighbour)) && gameMap.isWalkable(neighbour)) {
                    // Map each neighbour to the current location
                    parentMap.put(neighbour, current);
                    visitedSet.add(new HashLocation(neighbour));
                    queue.add(neighbour);
                }
            }
        }

        // Could not find a path
        return start;
    }

    // Checks the location against things to avoid. This is a simple implementation.
    public boolean shouldAvoid(Location location) {

        for (PathObject pathObject : avoidedObjects) {
            if (pathObject.getLocation().equals(location)) {
                return true;
            }
        }

        return false;
    }


    public void avoidPathObject(PathObject pathObject) {
        this.avoidedObjects.add(pathObject);
    }
}
