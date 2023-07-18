package src.Entity;

import src.Game;
import src.Item.Item;
import src.Item.ItemEventCode;
import src.Item.ItemEventListener;
import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * PacActor provides functionality and movement logic for PacMan, responding to input by users. It can also be
 * automatically controlled, either through a list of moves or an algorithm (provided by COMP30006 staff).
 * PacActor is currently the only entity who can consume items in the game.
 */
public class PacActor extends Entity implements ItemEventListener, GGKeyRepeatListener {
    private static final int NB_SPRITES = 4;
    private static final int KEY_REPEAT_PERIOD = 150;
    private static final String SPRITE_FILE_NAME = "sprites/pacpix.gif";
    private static final String EXPLOSION_EFFECT = "sprites/explosion3.gif";
    private ArrayList<Character> propertyMoves;
    private int propertyMoveIndex = 0;
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;
    private boolean isAuto = false;
    private boolean isAlive = true;

    /**
     * Constructor for a PacActor, requiring a game
     * @param game game that PacActor will be used in
     */
    public PacActor(Game game) {
        super(game, true, SPRITE_FILE_NAME, NB_SPRITES);
        propertyMoves = new ArrayList<Character>();
    }

    @Override
    public void start() {

        // If not set to auto, PacActor will respond to keyboard input
        if (!isAuto) {
            game.getGameGrid().addKeyRepeatListener(this);
            game.getGameGrid().setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
        }
    }

    /**
     * Used by COMP30006 for testing purposes
     */
    public void act()
    {
        if (!isAlive) return;

        show(idSprite);
        idSprite = (idSprite + 1) % NB_SPRITES;

        if (isAuto) {
            moveInAutoMode();
        }

        this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
    }

    /**
     * This function exists simply for staff. It has been modified as little as possible, despite the
     * desire to rewrite the entire thing. Especially the findClosestItem(), that one hurts
     */
    private void moveInAutoMode() {
        if (propertyMoves.size() > propertyMoveIndex) {
            followPropertyMoves();
            return;
        }

        Item closestItem = findClosestItem();
        double oldDirection = getDirection();

        Location.CompassDirection compassDir = lookAt4Compass(closestItem);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);

        if (!isVisited(next) && game.getGameGrid().isWalkable(next)) {
            setLocation(next);
        } else {
            setDirection(oldDirection);
            next = randomWalk();
            setDirection(getLocation().getDirectionTo(next));
            setLocation(next);
        }

        Item item = game.getGameGrid().getItemAt(getLocation());
        if (item != null)
            item.consume();

        addVisitedList(next);
    }

    /**
     * Used by COMP30006 staff
     * @return "closest" item
     */
    private Item findClosestItem() {
        int currentDistance = Integer.MAX_VALUE;
        Item closestItem = null;

        ArrayList<Item> items = game.getGameGrid().getAllItems();

        for (Item item: items) {
            // This would make more sense but due to testing, since this
            // was not functionality included with the original codebase
            // I cannot change it :(
//            if (item.isConsumed()) continue;
            Location itemLocation = item.getLocation();
            int distanceToItem = getLocation().getDistanceTo(itemLocation);
            if (distanceToItem < currentDistance) {
                closestItem = item;
                currentDistance = distanceToItem;
            }
        }

        return closestItem;
    }

    /**
     * Used by COMP30006 staff
     */
    private void followPropertyMoves() {
        Character currentMove = propertyMoves.get(propertyMoveIndex);
        switch(currentMove) {
            case 'R':
                turn(90);
                break;
            case 'L':
                turn(-90);
                break;
            case 'M':
                Location next = getNextMoveLocation();
                if (game.getGameGrid().isWalkable(next)) {
                    setLocation(next);
                    Item item = game.getGameGrid().getItemAt(next);
                    if (item != null) {
                        item.consume();
                    }
                }
                break;
            case 'S':
                break;
        }
        propertyMoveIndex++;
    }

    /**
     * Parses string of moves.
     * @param propertyMoveString string of moves
     */
    public void setPropertyMoves(String propertyMoveString) {
        if (propertyMoveString == null)
            return;

        char[] array = propertyMoveString.replace(",","").toCharArray();
        for (char move : array) {
            propertyMoves.add(move);
        }
    }

    /**
     * Moves PacActor according to keyboard input
     */
    @Override
    public void keyRepeated(int keyCode)
    {
        if (!isAlive)
            return;

        Location next = null;
        switch (keyCode)
        {
            case KeyEvent.VK_LEFT:
                next = getLocation().getNeighbourLocation(Location.WEST);
                setDirection(Location.WEST);
                break;
            case KeyEvent.VK_UP:
                next = getLocation().getNeighbourLocation(Location.NORTH);
                setDirection(Location.NORTH);
                break;
            case KeyEvent.VK_RIGHT:
                next = getLocation().getNeighbourLocation(Location.EAST);
                setDirection(Location.EAST);
                break;
            case KeyEvent.VK_DOWN:
                next = getLocation().getNeighbourLocation(Location.SOUTH);
                setDirection(Location.SOUTH);
                break;
        }
        if (game.getGameGrid().isWalkable(next))
        {
            setLocation(next);
            Item item = game.getGameGrid().getItemAt(next);
            if (item != null) {
                item.consume();
            }
        }
    }

    @Override
    public void onItemConsumed(ItemEventCode code, Location location) {

        // PacActor will respond to most items by increasing nbPills and score. For ice, nothing happens
        switch(code) {
            case IEC_PILL_CONSUMED:
                nbPills++;
                score++;
                game.getGameCallback().pacManEatPillsAndItems(location, "pills");
                break;
            case IEC_GOLD_CONSUMED:
                nbPills++;
                score += 5;
                game.getGameCallback().pacManEatPillsAndItems(location, "gold");
                break;
            case IEC_ICE_CONSUMED:
                game.getGameCallback().pacManEatPillsAndItems(location, "ice");
                break;
        }
        String title = game.getGameTitle() + " Current score: " + score;
        gameGrid.setTitle(title);
    }

    /**
     * Confirms that the monster provided has killed the PacActor
     * @param monster monster who has "killed" PacMan
     */
    public void confirmKill(Monster monster) {
        if (detectCollision(monster)) {
            onDeath();
        }
    }

    /**
     * Runs when PacActor has been killed. Places explosion sprite in its place
     */
    private void onDeath() {
        hide();
        isAlive = false;
        gameGrid.addActor(new Actor(EXPLOSION_EFFECT), getLocation());
    }
    public boolean isAlive() { return isAlive; }
    public void setAuto(boolean auto) { this.isAuto = auto; }
    public int getNbPills() { return nbPills; }
}
