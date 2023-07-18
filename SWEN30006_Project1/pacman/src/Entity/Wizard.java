package src.Entity;

import ch.aplu.jgamegrid.Location;
import src.Game;

import java.util.ArrayList;
import java.util.Collections;

public class Wizard extends Monster{
    public Wizard(Game game) {
        super(game, MonsterType.Wizard);
    }

    private boolean hasWallJumped = false;
    @Override
    protected Location walkApproach(boolean manualCall) {

        Location.CompassDirection[] compassDirections = Location.CompassDirection.values();
        ArrayList<Integer> randomIndices = new ArrayList<Integer>();
        for(int i=0; i<compassDirections.length; i++) {
            randomIndices.add(i);
        }
        Collections.shuffle(randomIndices, randomiser);

        // Randomly pick between 8 compass directions
        for (Integer randomIndex : randomIndices) {
            Location.CompassDirection chosenDirection = compassDirections[randomIndex];
            Location next = getLocation().getNeighbourLocation(chosenDirection);

            if (game.getGameGrid().isWalkable(next)) {
                return next;
            }

            // The cell in this direction contains a Wall. Check the cell proceeding
            // it in the same direction
            Location nextAfterNext = next.getNeighbourLocation(chosenDirection);

            // If nextAfterNext can be moved to, jump the wall
            if (game.getGameGrid().isWalkable(nextAfterNext)) {

                // Wizard can only do this once per tick
                hasWallJumped = true;
                return nextAfterNext;
            }
        }

        // We're stuck in a box with no way out
        return getLocation();
    }

    protected Location walkApproachFurious() {
        if (hasWallJumped) {

            // Second cell wasn't a wall, Wizard can only jump 1 cell per turn
            hasWallJumped = false;
            return getLocation();
        }

        Location next = getLocation().getNeighbourLocation(getDirection());

        if (game.getGameGrid().isWalkable(next)) {
            return next;
        }

        // The second cell in this direction contains a Wall. Check the cell proceeding
        // it in the same direction
        Location nextAfterNext = next.getNeighbourLocation(getDirection());

        // If nextAfterNext can be moved to, jump the wall
        if (game.getGameGrid().isWalkable(nextAfterNext)) {
            return nextAfterNext;
        }

        // Try again with original walkApproach
        return callWalkApproachManually();
    }
}
