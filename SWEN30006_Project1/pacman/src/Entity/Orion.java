package src.Entity;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.Item.Item;
import src.utility.HashLocation;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class Orion extends Monster{
    public Orion(Game game) {
        super(game, MonsterType.Orion);
    }

    ArrayList<Item> remainingGold;
    Item goldTarget;

    @Override
    public void start() {

        // Initialise Orion state
        remainingGold = new ArrayList<Item>();
        goldTarget = null;
        show();
    }

    @Override
    public Location walkApproach(boolean manualCall) {

        // If Orion has visited all gold locations, Orion must revisit them all again
        if (remainingGold.isEmpty()) {
            remainingGold.addAll(game.getGameGrid().getGold());
        }

        // Orion has reached its goldTarget, must find another
        if (goldTarget == null) {

            // Find gold piece, favour pieces that haven't been consumed
            ArrayList<Item> consumed = new ArrayList<Item>();
            ArrayList<Item> notConsumed = new ArrayList<Item>();

            for (Item gold : remainingGold) {
                if (gold.isConsumed())
                    consumed.add(gold);
                else
                    notConsumed.add(gold);
            }

            int randInt;

            // Randomly select gold piece, favouring those that aren't consumed
            if (!notConsumed.isEmpty()) {
                randInt = randomiser.nextInt(notConsumed.size());
                goldTarget = notConsumed.get(randInt);
            } else if (!consumed.isEmpty()) {
                randInt = randomiser.nextInt(consumed.size());
                goldTarget = consumed.get(randInt);
            }
        }

        // Try to find way to next gold piece
        Location currentLocation = getLocation();
        Location goldLocation = goldTarget.getLocation();

        // Path Finding to Gold Piece using Breadth First Search
        Location next = BFS(currentLocation, goldLocation);

        // If we have reached our goldTarget, we must find a new goldTarget
        if (goldLocation.equals(next)) {
            remainingGold.remove(goldTarget);
            goldTarget = null;
        }

        return next;
    }

    /**
     * Bread First Search
     * @param start start of search
     * @param end goal
     * @return cell to move to
     */
    private Location BFS(Location start, Location end) {

        // This could also work with 8 directions
        Location.CompassDirection[] compassDirections = {
                Location.CompassDirection.NORTH,
                Location.CompassDirection.EAST,
                Location.CompassDirection.SOUTH,
                Location.CompassDirection.WEST
        };

        // State used by algorithm
        Queue<Location> queue = new LinkedList<Location>();
        Set<HashLocation> visited = new HashSet<HashLocation>();
        Map<Location, Location> parentMap = new HashMap<Location, Location>();

        queue.add(start);
        visited.add(new HashLocation(start));

        // If the start and end are the same, return the start
        parentMap.put(start, start);

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

            // Search through adjacent locations
            for (Location.CompassDirection direction : compassDirections) {
                Location neighbour = current.getNeighbourLocation(direction);

                // HashLocation is needed because Location doesn't override hashCode() method
                // that HashSet<>() needs to work properly
                if (!visited.contains(new HashLocation(neighbour)) && game.getGameGrid().isWalkable(neighbour)) {

                    // Map each neighbour to the current location
                    parentMap.put(neighbour, current);
                    visited.add(new HashLocation(neighbour));
                    queue.add(neighbour);
                }
            }
        }

        // Could not find a path
        return start;
    }
}
