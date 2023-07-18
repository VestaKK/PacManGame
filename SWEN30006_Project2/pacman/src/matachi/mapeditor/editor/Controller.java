package matachi.mapeditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import logger.EditorErrorCallback;
import matachi.mapeditor.grid.Camera;
import matachi.mapeditor.grid.Grid;
import matachi.mapeditor.grid.GridCamera;
import matachi.mapeditor.grid.GridModel;
import matachi.mapeditor.grid.GridView;

import gamemap.*;
import pacman.Game;
import logger.GameCallback;
import pacman.utility.PropertiesLoader;

/**
 * Controller of the application.
 * 
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 * 
 */
public class Controller implements ActionListener, GUIInformation {

	/**
	 * The model of the map editor.
	 */
	private static final int DEFAULT_MAP_WIDTH = 20;
	private static final int DEFAULT_MAP_HEIGHT = 11;
	private static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test.properties";
	private Grid gridModel;
	private int gridWidth;
	private int gridHeight;
	private Tile selectedTile;
	private Camera camera;
	private GridView gridView;
	private View view;
	private EditorErrorCallback editorErrorCallback;

	/**
	 * Construct the controller.
	 */
	public Controller() {
		this.editorErrorCallback = new EditorErrorCallback();
		init(DEFAULT_MAP_WIDTH, DEFAULT_MAP_HEIGHT);
	}

	/**
	 * Construct the controller with a file path.
	 */
	public Controller(String path) {
		if (path == null)
			return;

		// Initialise state
		this.editorErrorCallback = new EditorErrorCallback();
		File file = new File(path);

			// Initialise the editor with the file
		if (file.isFile()) {
			init(file);

			// Otherwise test the game folder
		} else if (file.isDirectory()) {

			GameRunner gameRunner = new GameRunner(file, editorErrorCallback, DEFAULT_PROPERTIES_PATH);

			// If the game isn't valid return to default editor
			if (!gameRunner.validateGame()) {
				init(DEFAULT_MAP_WIDTH, DEFAULT_MAP_HEIGHT);
				return;
			}

				// If testing the game fails, load the file the gameRunner failed on
			if (!gameRunner.testGame()) {
				init(gameRunner.getCurrentMapFile());
				// Otherwise, the game was tested successfully, and we return to default editor
			} else {
				init(DEFAULT_MAP_WIDTH, DEFAULT_MAP_HEIGHT);
			}

		}
	}

	public void init(int width, int height) {
		this.gridModel = new GridModel(width, height, TileManager.getInstance().getDefaultChar());
		this.gridWidth = gridModel.getWidth();
		this.gridHeight = gridModel.getHeight();
		this.camera = new GridCamera(gridModel, gridModel.getWidth(), gridModel.getHeight());
		this.gridView = new GridView(this, camera, TileManager.getInstance().getTiles()); // Every tile is 30x30 pixels
		this.view = new View(this, camera, gridView, TileManager.getInstance().getTiles());
	}

	public void init(File mapFile) {

		// Create model based on map File
		this.gridModel = GridModelLoader.createModel(mapFile);
		GameMapValidator gameMapValidator = new GameMapValidator(mapFile, editorErrorCallback);
		gameMapValidator.validateMap();
		// Set Grid Width and Height
		this.gridWidth = gridModel.getWidth();
		this.gridHeight = gridModel.getHeight();

		// Create Camera based off our model
		this.camera = new GridCamera(gridModel, gridModel.getWidth(), gridModel.getHeight());

		// Create grid based off our camera
		gridView = new GridView(this, camera, TileManager.getInstance().getTiles()); // Every tile is 30x30 pixels

		// Create a new View
		this.view = new View(this, camera, gridView, TileManager.getInstance().getTiles());
	}


	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : TileManager.getInstance().getTiles()) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}

		if (e.getActionCommand().equals("save")) {
			saveFileAction();
		} else if (e.getActionCommand().equals("load")) {
			loadFileAction();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWidth, gridHeight);
		}
	}

	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
		view.setSize(width, height);
	}

	DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWidth = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWidth = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWidth = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	private void saveFileAction() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File savedFile = chooser.getSelectedFile();
			GridModelLoader.saveModel(savedFile, gridModel);
			GameMapValidator gameMapValidator = new GameMapValidator(savedFile, editorErrorCallback);
			gameMapValidator.validateMap();
		}
	}

	public void loadFileAction() {
		JFileChooser chooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			view.close(); // Close the current view because it's no longer relevant
			File selectedFile = chooser.getSelectedFile();
			GameMapValidator gameMapValidator = new GameMapValidator(selectedFile, editorErrorCallback);
			gameMapValidator.validateMap();
			init(selectedFile);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}
