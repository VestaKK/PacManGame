// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.Entity.*;
import src.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Game class acts as a manager between the game grid and entities. It is responsible for loading
 * information from properties into the game grid and entities, as well as adding entities to the gameGrid
 * and subscribing entities to relevant itemHandlers. It also manages the game-loop i.e. starting the game-loop
 * and polling for when the game-loop should end. Additionally, Game provides useful data and functions for use
 * by entities.
 */
public class Game
{
  private static final String GAME_TITLE = "[PacMan in the Multiverse]";
  private static final int SLOW_DOWN_FACTOR = 3;
  private PacManGameGrid gameGrid;
  private PacActor pacActor;
  private GameCallback gameCallback;
  private Monster[] monsterArray;
  private ArrayList<Monster> activeMonsters;
  private GameVersion version = GameVersion.SIMPLE;
  private boolean isRunning = false;

  /**
   * Constructs a game, using the properties provided.
   * @param gameCallback  Used for testing and logging
   * @param properties Used for initialising the game-state
   */
  public Game(GameCallback gameCallback, Properties properties)
  {
    // Set up game
    this.gameCallback = gameCallback;
    String versionString = properties.getProperty("version");

    for (GameVersion gameType :GameVersion.values()) {
      version = versionString.equals(gameType.toString()) ? gameType : version;
    }

    // Construct game-related state
    gameGrid = new PacManGameGrid();
    pacActor = new PacActor(this);

    if (version == GameVersion.SIMPLE) {
      monsterArray = new Monster[] {
              new Troll(this),
              new TX5(this),
      };
    } else if (version == GameVersion.MULTIVERSE) {
      monsterArray = new Monster[] {
              new Troll(this),
              new TX5(this),
              new Alien(this),
              new Orion(this),
              new Wizard(this)
      };
    }

    // Monsters that start in valid positions will be added to this list
    activeMonsters = new ArrayList<Monster>();

    // Load items like pills and gold into the gameGrid
    loadItemsIntoGameGrid(properties);

    // Set up Random seeds
    int seed = Integer.parseInt(properties.getProperty("seed"));

    // Set up PacActor
    pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
    pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
    pacActor.setSeed(seed);
    pacActor.setSlowDown(SLOW_DOWN_FACTOR);
    gameGrid.subscribeToPillHandler(pacActor);
    gameGrid.subscribeToGoldHandler(pacActor);

    // Set up Monsters
    for (Monster monster : monsterArray) {
      monster.setSeed(seed);
      monster.setSlowDown(SLOW_DOWN_FACTOR);
      gameGrid.subscribeToIceHandler(monster);
      gameGrid.subscribeToGoldHandler(monster);
    }

    // Add entities to the gameGrid, if their locations are valid
    addEntitiesToGameGrid(properties);
  }
  public void run() {

    // Make sure no one's running the game twice
    if (isRunning) return;
    else isRunning = true;

    // Render initial frame and start the game
    gameGrid.drawGrid();
    gameGrid.setSimulationPeriod(100);
    gameGrid.setTitle(GAME_TITLE);
    gameGrid.doRun();
    gameGrid.show();

    // Run the start methods for all the entities
    pacActor.start();
    for (Monster monster : monsterArray) {
      monster.start();
    }

    int maxPillsAndItems =
            gameGrid.getPills().size() +
            gameGrid.getGold().size();

    // Loop to look for collision in the application thread
    // This makes it improbable that we miss a hit
    while(pacActor.isAlive()) {

      // If Pacman collects all the pills the game ends
      if(pacActor.getNbPills() >= maxPillsAndItems) {
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
    for (Monster monster : monsterArray) {
      monster.setStatePause();
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
    gameGrid.doPause();
  }

  /**
   * Parse information about starting entity locations into usable data, and add entities to
   * the game grid accordingly.
   * @param properties properties with relevant data
   * */
  private void addEntitiesToGameGrid(Properties properties) {

    // Parse properties for Pacman
    String[] pacManStartString = properties.getProperty("PacMan.location").split(",");
    int pacManX = Integer.parseInt(pacManStartString[0]);
    int pacManY = Integer.parseInt(pacManStartString[1]);
    Location pacStart = new Location(pacManX, pacManY);

    // Can't play the game if Pacman isn't in the playable area
    assert gameGrid.isInGrid(pacStart): "Pacman does not have a valid starting position";
    gameGrid.addActor(pacActor, pacStart);

    // Parse properties for monsters
    for (Monster monster : monsterArray) {
      String[] monsterStartString = properties.getProperty(monster.getType().toString() + ".location").split(",");
      int monsterX = Integer.parseInt(monsterStartString[0]);
      int monsterY = Integer.parseInt(monsterStartString[1]);
      Location monsterStart = new Location(monsterX, monsterY);

      if (gameGrid.isInGrid(monsterStart)) {
        activeMonsters.add(monster);
        gameGrid.addActor(monster, monsterStart, Location.NORTH);
      }
    }

  }

  /**
   * Parse information about item locations into usable data and give that information
   * to the game grid.
   * @param properties properties with relevant data
   */
  private void loadItemsIntoGameGrid(Properties properties) {
    ArrayList<Location> propertyPillLocations = new ArrayList<Location>();
    ArrayList<Location> propertyGoldLocations = new ArrayList<Location>();
    String pillsLocationString = properties.getProperty("Pills.location");
    String goldLocationString = properties.getProperty("Gold.location");

    if (pillsLocationString != null) {
      String[] singlePillLocationStrings = pillsLocationString.split(";");
      for (String singlePillLocationString: singlePillLocationStrings) {
        String[] locationStrings = singlePillLocationString.split(",");
        propertyPillLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }

    if (goldLocationString != null) {
      String[] singleGoldLocationStrings = goldLocationString.split(";");
      for (String singleGoldLocationString: singleGoldLocationStrings) {
        String[] locationStrings = singleGoldLocationString.split(",");
        propertyGoldLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
      }
    }

    gameGrid.setUpGrid(propertyPillLocations, propertyGoldLocations);
  }

  public PacActor getPacActor() { return pacActor; }
  public GameCallback getGameCallback() { return gameCallback; }
  public PacManGameGrid getGameGrid() { return gameGrid; }
  public GameVersion getGameVersion() { return version; }
  public String getGameTitle() { return GAME_TITLE; };
}
