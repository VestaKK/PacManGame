package src.Entity;

import ch.aplu.jgamegrid.Location;
import src.Game;

public class TX5 extends Monster {
    private static final int START_IDLE_TIME = 5;
    public TX5(Game game) {
        super(game, MonsterType.TX5);
    }

    @Override
    public void start() {

        // TX5 will stay idle for 5 seconds after the game has started
        setStateFrozen(START_IDLE_TIME);
        show();
    }

    @Override
    protected Location walkApproach(boolean manualCall) {

        // Determine direction to pacActor and try to move in that direction.
        // Otherwise, random walk.
        Location.CompassDirection compassDir = lookAt4Compass(game.getPacActor());
        Location next = getLocation().getNeighbourLocation(compassDir);

        if (!isVisited(next) && game.getGameGrid().isWalkable(next))
        {
            return next;
        } else {
            return randomWalk();
        }
    }
}
