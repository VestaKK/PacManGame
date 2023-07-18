package pathfinder;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

/**
 * An adapter for a pathObject that takes in an actor
 */
public class PathObjectActorAdapter implements PathObject{
    private Actor actor;
    public PathObjectActorAdapter(Actor actor) {
        this.actor = actor;
    }
    @Override
    public Location getLocation() {
        return actor.getLocation();
    }
}
