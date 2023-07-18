package matachi.mapeditor.grid;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;

import matachi.mapeditor.editor.GUIInformation;
import matachi.mapeditor.editor.Tile;
import matachi.mapeditor.editor.TileManager;

/**
 * A class which shows a Grid graphically as a JPanel.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class GridView extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -345930170664066299L;
	
	/**
	 * A reference to the model. Needed to query data.
	 */
	private Camera camera;
	
	/**
	 * References to all tiles.
	 */
	private GridTile[][] map;
	
	/**
	 * Available tiles.
	 */
	private List<? extends Tile> tiles;
	
	/**
	 * Creates a grid panel.
	 * @param guiInformation Information from the GUI that the grid requires.
	 * @param camera The camera that should be represented graphically in the GridView.
	 * @param tiles List of available tiles.
	 */
	public GridView(GUIInformation guiInformation, Camera camera, List<? extends Tile> tiles) {
		super(new GridLayout(camera.getModelHeight(), camera.getModelWidth()));
		
		this.tiles = tiles;
		this.camera = camera;
		this.camera.addPropertyChangeListener(this);
		GridController controller = new GridController(camera, guiInformation);
		this.addMouseListener(controller);
		this.addMouseMotionListener(controller);
		
		/** Add all tiles to the grid. */
		map = new GridTile[camera.getHeight()][camera.getWidth()];
		for (int y = 0; y < camera.getHeight(); y++) {
			for (int x = 0; x < camera.getWidth(); x++) {

				// Initialise based of camera model (Because it makes more sense than going off random ass defaults)
				Tile gridTile = TileManager.getInstance().getTile(camera.getTile(x, y));
				map[y][x] = new GridTile(gridTile);
				map[y][x].addKeyListener(controller);
				map[y][x].setFocusable(true);
				this.add(map[y][x]);

			}
		}
	}
	
	/**
	 * Update the grid.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("movedCamera")) {
			redrawGrid();
		} else if (evt.getPropertyName().equals("changedTile")) {
			redrawTile((Point) evt.getNewValue());
		}
	}
	
	/**
	 * Redraw the whole grid.
	 */
	public void redrawGrid() {
		for (int y = 0; y < camera.getHeight(); y++) {
			for (int x = 0; x < camera.getWidth(); x++) {
				for (Tile t : tiles) {
					if (camera.getTile(x, y) == t.getCharacter()) {
						map[y][x].setTile(t);
						map[y][x].grabFocus();
					}
				}
			}
		}
		this.repaint();
	}
	
	/**
	 * Redraw a single tile.
	 * @param position The tile's position in the grid.
	 */
	private void redrawTile(Point position) {
		for (Tile t : tiles) {
			if (camera.getTile(position.x, position.y) == t.getCharacter()) {
				map[position.y][position.x].setTile(t);
			}
		}
	}
	
	/**
	 * How the tiles are represented graphically.
	 */
	private class GridTile extends JPanel {
		
		private static final long serialVersionUID = 8127828009105626334L;
		
		/**
		 * The tile that the GridTile should show.
		 */
		private Tile tile;

		/**
		 * Creates a GridTIle with a new tile that it should show.
		 * @param tile
		 */
		public GridTile(Tile tile) {
			this.tile = tile;
		}

		/**
		 * Give the JPanel GridTile a new tile that it should show.
		 * @param tile
		 */
		public void setTile(Tile tile) {
			this.tile = tile;
			this.repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(tile.getImage(), 0, 0, null);
		}
	}
}
