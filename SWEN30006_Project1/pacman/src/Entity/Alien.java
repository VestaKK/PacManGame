package src.Entity;

import ch.aplu.jgamegrid.Location;
import src.Game;
import java.util.ArrayList;

public class Alien extends Monster{
    public Alien(Game game) {
        super(game, MonsterType.Alien);
    }
    @Override
    protected Location walkApproach(boolean manualCall) {
        Location.CompassDirection chosenDir;

        // Check each 8 compass directions
        ArrayList<Location.CompassDirection> possibleDirections = new ArrayList<Location.CompassDirection>();
        int distanceToPac = Integer.MAX_VALUE;

        // Iterate through each compass direction and determine the shortest distance to PacMan
        for(Location.CompassDirection compassDir: Location.CompassDirection.values()){
            Location next = getLocation().getNeighbourLocation(compassDir);

            // Can't walk through a wall
            if(!game.getGameGrid().isWalkable(next))
                continue;

            int newDistance = next.getDistanceTo(game.getPacActor().getLocation());

            if (newDistance < distanceToPac) {
                distanceToPac = newDistance;

                // New shortest distance found, so we clear the list of possible directions
                possibleDirections.clear();
                possibleDirections.add(compassDir);

                // Any cells equidistant to PacMan is added to a list
            } else if (newDistance == distanceToPac){
                possibleDirections.add(compassDir);
            }
        }

        // No direction can be walked to i.e. Alien is in a box
        if(possibleDirections.size() == 0)
            return getLocation();

        // Choose Direction to move in randomly
        int randInt = randomiser.nextInt(possibleDirections.size());
        chosenDir = possibleDirections.get(randInt);

        return getLocation().getNeighbourLocation(chosenDir);
    }
}
