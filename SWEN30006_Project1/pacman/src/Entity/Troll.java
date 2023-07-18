package src.Entity;

import ch.aplu.jgamegrid.Location;
import src.Game;

public class Troll extends Monster{
    public Troll(Game game) {
        super(game, MonsterType.Troll);
    }
    @Override
    protected Location walkApproach(boolean manualCall) {

        // Troll walks in a random direction
        return randomWalk();
    }
}
