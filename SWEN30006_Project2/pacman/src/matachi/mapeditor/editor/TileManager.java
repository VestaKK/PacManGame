package matachi.mapeditor.editor;

import com.sun.source.tree.Tree;

import java.io.File;
import java.util.*;

/**
 * This class supports the Tile list with methods.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class TileManager {

	/**
	 * Returns a list with Tiles, constructed with images from the given folderPath.
	 * @param folderPath Path to image folder.
	 * @return List<Tile> List of tiles.
	 */
	private static final int TILE_WIDTH = 32;
	private static final int TILE_HEIGHT = TILE_WIDTH;
	private static String folderPath = "pacman/sprites/data/";
	private static TileManager instance;
	private TreeMap<Character, Tile> char2TileMap;
	private HashMap<Tile, Character> tile2CharMap;
	private Tile defaultTile;
	private TileManager() {

		// Essentially, make a tile map
		char2TileMap = createC2TMap(folderPath);
		tile2CharMap = createT2CMap(char2TileMap);
	}

	private TreeMap<Character, Tile> createC2TMap(final String folderPath) {

		TreeMap map = new TreeMap<Character, Tile>();
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {

			Tile newTile = new Tile(file, this);

			if (defaultTile == null) {
				defaultTile = newTile;
			}

			map.put(file.getName().charAt(0), newTile);
		}

		return map;
	}

	private HashMap<Tile, Character> createT2CMap(TreeMap<Character, Tile> tMap) {

		HashMap<Tile, Character> map = new HashMap<Tile, Character>();
		Set<Character> keySet = tMap.keySet();

		for (Character key : keySet.stream().toList()) {
			Tile value = tMap.get(key);
			map.put(value, key);
		}

		return map;
	}

	public static TileManager getInstance() {
		if (instance == null) {
			instance = new TileManager();
		}
		return instance;
	}

	public char getDefaultChar() { return defaultTile.getCharacter(); }
	public List<Tile> getTiles() {
		return instance.char2TileMap.values().stream().toList();
	}
	public char getChar(Tile tile) {
		return tile2CharMap.get(tile).charValue();
	}
	public Tile getTile(char tileChar) {
		return char2TileMap.get(tileChar);
	}
	public static int getTileHeight() {
		return TILE_HEIGHT;
	}
	public static int getTileWidth() {
		return TILE_WIDTH;
	}
}
