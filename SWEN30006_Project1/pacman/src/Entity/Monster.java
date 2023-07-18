package src.Entity;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.GameVersion;
import src.Item.ItemEventCode;
import src.Item.ItemEventListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Monsters chase PacMan down, responding to the items he consumes. Classes that inherit from Monster
 * must implement walkApproach(), but may also override walkApproachFurious() for extra functionality
 */
public abstract class Monster extends Entity implements ItemEventListener {
    private static final int TOTAL_TIME_FURIOUS = 3;
    private static final int TOTAL_TIME_FROZEN = 3;
    private final MonsterType type;
    private boolean isFurious= false;
    private boolean isFrozen = false;

    /**
     * Constructs a Monster to be used in the provided game, with a MonsterType
     * (Only because GameCallBack requires it)
     * @param game game using this Monster
     * @param type type of monster
     */
    protected Monster(Game game, MonsterType type) {
        super(game, type.getImageName());
        this.type = type;
    }
    public void act()
    {

        // Can't move when frozen
        if (isFrozen) {
            return;
        }

        // Monsters move one cell per tick
        // walkApproach() is called at least once a tick
        Location next = walkApproach(false);

        // Maintain a list of previously visited locations
        addVisitedList(next);

        // Retains facing direction if monster hasn't moved a cell
        // Else turn and move to the desired cell
        if (!getLocation().equals(next)) {
            setDirection(getLocation().getDirectionTo(next));
            setLocation(next);
        }

        // If the Monster is Furious, it moves one more cell
        if(isFurious) {
            Location secondCell = walkApproachFurious();

            // Add second cell to visited list as well
            addVisitedList(secondCell);

            // Retains facing direction if monster hasn't moved a cell
            // Else turn and move to the desired cell
            if (!getLocation().equals(secondCell)) {
                setDirection(getLocation().getDirectionTo(secondCell));
                setLocation(secondCell);
            }
        }

        if (getDirection() > 150 && getDirection() < 210)
            setHorzMirror(false);
        else
            setHorzMirror(true);
        game.getGameCallback().monsterLocationChanged(this);
    }

    /**
     * Behaviour to be implemented by different Monsters. Monster
     * subclasses should calculate a single cell to move to. Any rotation performed
     * by a Monster in this function will be ignored, unless they are stationary.
     * @param manualCall true if the function was manually called, else false
     * @return cell to move to
     */
    protected abstract Location walkApproach(boolean manualCall);

    /**
     * Calls walkApproach() manually
     * @return
     */
    protected Location callWalkApproachManually() { return walkApproach(true);}

    /**
     * Default approach to movement when furious. Any rotation performed by a Monster
     * in this function will be ignored, unless they are stationary.
     * @return cell to move to
     */
    protected Location walkApproachFurious() {
        Location next = getNextMoveLocation();
        if (game.getGameGrid().isWalkable(next))
            return next;
        else
            return callWalkApproachManually();
    }
    @Override
    public void onItemConsumed(ItemEventCode code, Location location) {
        if (game.getGameVersion() == GameVersion.SIMPLE) return;

        switch(code) {
            // Monsters will become furious when a gold is consumed
            // This is not the case when the monster is frozen
            case IEC_GOLD_CONSUMED:
                if (!isFrozen)
                    setStateFurious(TOTAL_TIME_FURIOUS);
                break;

            // Monsters will stop in place when an ice is consumed
            // Can be frozen while furious
            case IEC_ICE_CONSUMED:
                setStateFrozen(TOTAL_TIME_FROZEN);
                break;
        }
    }

    /**
     * Sets the monster's state to Frozen for a period of time
     * @param seconds number of seconds to stay frozen
     */
    protected void setStateFrozen(int seconds) {
        this.isFrozen = true;
        Timer timer = new Timer(); // Instantiate Timer Object
        int SECOND_TO_MILLISECONDS = 1000;
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.isFrozen = false;
            }
        }, seconds * SECOND_TO_MILLISECONDS);
    }

    /**
     * Sets th monster's state to Furious for a period of time
     * @param seconds number of seconds to stay furious
     */
    protected void setStateFurious(int seconds) {
        this.isFurious = true;
        Timer timer = new Timer(); // Instantiate Timer Object
        int SECOND_TO_MILLISECONDS = 1000;
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.isFurious = false;
            }
        }, seconds * SECOND_TO_MILLISECONDS);
    }
    public void setStatePause() { this.isFrozen = true; }
    public MonsterType getType() { return type; }
    public boolean isFurious() { return isFurious; }
}
