package pacman.Entity;

import pacman.Game;
import pacman.Item.Item;
import pacman.Item.ItemEventCode;
import pacman.Item.ItemEventListener;
import ch.aplu.jgamegrid.*;
import pathfinder.AutoPlayer;
import pathfinder.PathObject;
import pathfinder.PathObjectActorAdapter;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 * PacActor provides functionality and movement logic for PacMan, responding to input by users. It can also be
 * automatically controlled, either through a list of moves or an algorithm (provided by COMP30006 staff).
 * PacActor is currently the only entity who can consume items in the game.
 */
public class PacActor extends Entity implements ItemEventListener, GGKeyRepeatListener {
    private static final int NB_SPRITES = 4;
    private static final int KEY_REPEAT_PERIOD = 150;
    private static final String SPRITE_FILE_NAME = "pacman/sprites/pacpix.gif";
    private static final String EXPLOSION_EFFECT = "pacman/sprites/explosion3.gif";
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;
    private boolean isAuto = false;
    private boolean isAlive = true;
    private AutoPlayer autoPlayer;

    /**
     * Constructor for a PacActor, requiring a game
     * @param game game that PacActor will be used in
     */
    public PacActor(Game game) {
        super(game, true, SPRITE_FILE_NAME, NB_SPRITES);
    }

    @Override
    public void start() {

        // If not set to auto, PacActor will respond to keyboard input
        if (!isAuto) {
            gameGrid.addKeyRepeatListener(this);
            gameGrid.setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
        } else {
            autoPlayer = new AutoPlayer(game.getGameMap());

//            for (Monster monster : game.getActiveMonsters()) {
//                autoPlayer.avoidPathObject(new PathObjectActorAdapter(monster));
//            }

//            for (Item item : game.getGameMap().getIce()) {
//                autoPlayer.avoidPathObject(new PathObjectActorAdapter(item));
//            }
        }
    }

    /**
     * Used by COMP30006 for testing purposes
     */
    public void act()
    {
        if (!isAlive || gameGrid.isPaused())
            return;

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

        Item closestItem = findClosestItem();

        if (closestItem == null)
            return;

        Location next = autoPlayer.findNext(getLocation(), closestItem.getLocation());

        if (!next.equals(getLocation())) {
            setDirection(getLocation().get4CompassDirectionTo(next).getDirection());
            game.getGameMap().moveTo(next, this);
        }
    }
    /**
     * Used by COMP30006 staff
     * @return "closest" item
     */
    private Item findClosestItem() {
        int currentDistance = Integer.MAX_VALUE;
        Item closestItem = null;

        // Get array of pills and golds
        ArrayList<Item> items = new ArrayList<Item>();
        items.addAll(game.getGameMap().getPills());
        items.addAll(game.getGameMap().getGold());

        for (Item item: items) {
            if (item.isConsumed())
                continue;

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
     * Moves PacActor according to keyboard input
     */
    @Override
    public void keyRepeated(int keyCode)
    {
        if (!isAlive || gameGrid.isPaused())
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

        if (next == null)
            return;

        if (game.getGameMap().isWalkable(next))
        {
            game.getGameMap().moveTo(next, this);
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
        this.hide();
        isAlive = false;
        gameGrid.addActor(new Actor(EXPLOSION_EFFECT), getLocation());
    }
    public boolean isAlive() { return isAlive; }
    public void setAuto(boolean auto) { this.isAuto = auto; }
    public int getNbPills() { return nbPills; }
}
