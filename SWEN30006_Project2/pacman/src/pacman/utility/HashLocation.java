package pacman.utility;

import ch.aplu.jgamegrid.Location;
import java.util.Objects;

/**
 * Only created because Location uses the default hashCode()
 */
public class HashLocation extends Location{
    public HashLocation() {
        super();
    }
    public HashLocation(int x, int y) {
        super(x,y);
    }
    public HashLocation(Location location) {
        super(location);
    }
    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
    public boolean equals(Object e) {
        if (e.getClass() != HashLocation.class) {
            return false;
        } else {
            HashLocation other = (HashLocation) e;
            return other.x == this.x &&
                    other.y == this.y;
        }
    }
}
