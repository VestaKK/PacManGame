package src.Entity;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import src.Game;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Entity class is managed and created by the game class, requiring a game to be passed through its constructor.
 * Provides functionality used by both the Game and subclasses e.g. randomiser, visitedList, setSeed(), randomWalk()
 * etc.
 */
public abstract class Entity extends Actor {
    protected Random randomiser = new Random();
    protected Game game;
    private ArrayList<Location> visitedList = new ArrayList<Location>();
    private int LIST_LENGTH = 10;

    /**
     * Constructs an entity from the provided game.
     * @param game game that the entity will be located in
     * @param isRotateable whether the entity's sprite can rotate
     * @param spriteFileName filename to image(s)
     * @param nbSprites number of image(s)
     */
    protected Entity(Game game, Boolean isRotateable, String spriteFileName, int nbSprites) {
        super(isRotateable, spriteFileName, nbSprites);
        this.game = game;
    }

    /**
     * Constructs an entity from the provided game.
     * @param game game that the entity will be located in
     * @param spriteFileName filename to image
     */
    protected Entity(Game game, String spriteFileName) {
        super(spriteFileName);
        this.game = game;
    }

    /**
     * Sets the seed of the Entity's randomiser
     * @param seed integer seed
     */
    public void setSeed(int seed) {
        randomiser.setSeed(seed);
    }

    /**
     * start() is run exactly once before the execution of the
     * game loop
     */
    public void start() {
        // Show by default
        show();
    }

    /**
     * Determines collision with the provided Entity.
     * @param entity Entity in the same game
     * @return true if a collision is detected, else false
     */
    public boolean detectCollision(Entity entity) {
        return this.getLocation().equals(entity.getLocation());
    }

    /**
     * Calculates compass direction to the Actor in 4 cardinal directions
     * @param actor Actor in the same game
     * @return Compass direction (NORTH, SOUTH, EAST, WEST)
     */
    public Location.CompassDirection lookAt4Compass(Actor actor) {
        return getLocation().get4CompassDirectionTo(actor.getLocation());

    }

    /**
     * Adds location to a fixed-sized list of previously visited locations
     * @param location visited location
     */
    protected void addVisitedList(Location location)
    {
        visitedList.add(location);
        if (visitedList.size() == LIST_LENGTH)
            visitedList.remove(0);
    }

    /**
     * Checks if the location has been visited before
     * @param location location to be checked
     * @return true if the location has been visited, else false
     */
    protected boolean isVisited(Location location)
    {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return true;
        return false;
    }

    /**
     * Generates a random location to walk to.
     * @return random, walkable location
     */
    protected Location randomWalk() {
        double initialDirection = getDirection();
        // Randomly select left or right turn
        int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;

        // Turn left or right randomly
        setDirection(initialDirection);
        turn(sign * 90);
        Location next = getNextMoveLocation();

        if (game.getGameGrid().isWalkable(next)) {
            setDirection(initialDirection);
            return next;
        }

        // Try to go forwards
        setDirection(initialDirection);
        next = getNextMoveLocation();

        if (game.getGameGrid().isWalkable(next)) {
            setDirection(initialDirection);
            return next;
        }

        // Try the other turning direction
        setDirection(initialDirection);
        turn(-sign * 90);
        next = getNextMoveLocation();

        if (game.getGameGrid().isWalkable(next)) {
            setDirection(initialDirection);
            return next;
        }

        // Try to go backwards
        setDirection(initialDirection);
        turn(180);
        next = getNextMoveLocation();


        // This statement should always be true given
        // the maze
        if (game.getGameGrid().isWalkable(next)) {
            setDirection(initialDirection);
            return next;
        }

        // Somehow we are in a box
        setDirection(initialDirection);
        return getLocation();
    }
}
