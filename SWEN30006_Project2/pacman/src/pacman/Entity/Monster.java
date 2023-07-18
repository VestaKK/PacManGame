package pacman.Entity;

import ch.aplu.jgamegrid.Location;
import pacman.Game;
import pacman.Item.ItemEventCode;
import pacman.Item.ItemEventListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Monsters chase PacMan down. Classes that inherit from
 * Monster must implement walkApproach().
 */
public abstract class Monster extends Entity {
    private static final int TOTAL_TIME_FURIOUS = 3;
    private static final int TOTAL_TIME_FROZEN = 3;
    private final MonsterType type;
    private boolean isPaused= false;

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
        if (isPaused || gameGrid.isPaused()) {
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
            game.getGameMap().moveTo(next, this);
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

    protected void setStatePaused(int seconds) {
        this.isPaused = true;
        Timer timer = new Timer(); // Instantiate Timer Object
        int SECOND_TO_MILLISECONDS = 1000;
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.isPaused = false;
            }
        }, seconds * SECOND_TO_MILLISECONDS);
    }
    public void setStatePaused() { this.isPaused = true; }
    public MonsterType getType() { return type; }
}
