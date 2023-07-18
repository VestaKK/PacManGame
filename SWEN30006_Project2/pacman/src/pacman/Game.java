// PacMan.java
// Simple PacMan implementation
package pacman;

import ch.aplu.jgamegrid.*;
import gamemap.GameMap;
import logger.GameCallback;
import pacman.Entity.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Game class instantiates various entities and plays out a game
 * based on the given GameMap. Game manages collisions between monsters
 * and Pacman
 */
public class Game
{
  private static final String GAME_TITLE = "[PacMan in the TorusVerse]";
  private static final int SLOW_DOWN_FACTOR = 3;
  private PacActor pacActor;
  private GameCallback gameCallback;
  private Monster[] monsterArray;
  private ArrayList<Monster> activeMonsters;
  private GameMap gameMap;
  private GameGrid gameGrid;
  private boolean isRunning = false;

  /**
   * Constructs a game, using the properties provided.
   * @param gameCallback  Used for testing and logging
   * @param properties Used for initialising the game-state
   */
  public Game(GameCallback gameCallback, Properties properties, GameMap gameMap) {
    this.gameCallback = gameCallback;
    this.gameGrid = gameMap.createGameGrid();
    this.gameMap = gameMap;

    int seed = Integer.parseInt(properties.getProperty("seed"));
    boolean auto = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));

    this.pacActor = new PacActor(this);
    this.pacActor.setAuto(auto);
    this.pacActor.setSeed(seed);
    this.pacActor.setSlowDown(SLOW_DOWN_FACTOR);
    this.gameGrid.addActor(pacActor, gameMap.getPacStart());
    gameMap.subscribeToPillHandler(this.pacActor);
    gameMap.subscribeToGoldHandler(this.pacActor);

    this.activeMonsters = new ArrayList<Monster>();
    for (Location location : gameMap.getTx5StartLocations()) {
      TX5 tx5 = new TX5(this);
      tx5.setSeed(seed);
      tx5.setSlowDown(SLOW_DOWN_FACTOR);
      this.gameGrid.addActor(tx5, location);
      this.activeMonsters.add(tx5);
    }

    for (Location location : gameMap.getTrollStartLocations()) {
      Troll troll = new Troll(this);
      troll.setSeed(seed);
      troll.setSlowDown(SLOW_DOWN_FACTOR);
      this.gameGrid.addActor(troll, location);
      this.activeMonsters.add(troll);
    }
  }

  /**
   * Runs the game
   * @return Whether Pacman won the game or not
   */
  public boolean run() {

    // Make sure no one's running the game twice
    if (isRunning) return false;
    else isRunning = true;

    gameGrid.setSimulationPeriod(100);
    gameGrid.setTitle(GAME_TITLE);
    gameGrid.doRun();
    gameGrid.show();

    // Run the start methods for all the entities
    pacActor.start();
    for (Monster monster : activeMonsters) {
      monster.start();
    }

    int maxPillsAndGold =
            gameMap.getPills().size() +
            gameMap.getGold().size();

    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    while(pacActor.isAlive()) {

      // If Pacman collects all the pills the game ends
      if (pacActor.getNbPills() >= maxPillsAndGold) {
        break;
      }

      for (Monster monster : activeMonsters) {
        if (monster.detectCollision(pacActor))
          pacActor.confirmKill(monster);
      }
      gameGrid.delay(10);
    }
    gameGrid.delay(120);

    // Once game is over we pause all the monsters
    for (Monster monster : activeMonsters) {
      monster.setStatePaused();
    }

    // Change screen based on pacActor.isAlive()
    String title;
    if (pacActor.isAlive()) {
      gameGrid.getBg().setPaintColor(Color.yellow);
      gameGrid.setTitle(title = "YOU WIN");
    } else {
      gameGrid.getBg().setPaintColor(Color.red);
      gameGrid.setTitle((title = "GAME OVER"));
    }
    gameCallback.endOfGame(title);
    gameGrid.stopGameThread();
    gameGrid.doPause();

    return pacActor.isAlive();
  }

  public PacActor getPacActor() { return pacActor; }
  public ArrayList<Monster> getActiveMonsters() { return new ArrayList<Monster>(activeMonsters); }
  public GameCallback getGameCallback() { return gameCallback; }
  public GameMap getGameMap() { return gameMap; }
  public String getGameTitle() { return GAME_TITLE; }

  public void close() {
    gameGrid.getFrame().dispose();
  }

}
