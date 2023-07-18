package matachi.mapeditor.editor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A class that holds a image, and a character that will be written to the
 * map file.
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class Tile {

	/**
	 * The character that will be used in the map file when saved.
	 */

	/**
	 * The image that will be used in the editor.
	 */
	private BufferedImage image;
	private TileManager tileManager;
	private String filePath;
	private String tileName;

	public Tile(File file, TileManager tileManager) {
		this.tileManager = tileManager;
		this.filePath = file.getPath();
		// Generate Tile Name
		String tileString = file.getName();

		Character capital = tileString.charAt(tileString.indexOf('_') + 1);

		this.tileName =
					Character.toUpperCase(capital) +
					tileString.substring(tileString.indexOf('_') + 2,
										tileString.indexOf('.'));

		// Get Funny Image
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println("Bad file path: " + file.getPath());
			System.exit(0);
		}
	}

	public char getCharacter() {
		return tileManager.getChar(this);
	}

	public String getTileName() {
		return tileName;
	}

	public String getFilePath() {
		return filePath;
	}
	/**
	 * Get the tile as a image.
	 * @return Image The tile icon.
	 */
	public Image getImage() {
		return deepCopy(image);
	}

	/**
	 * Get the tile as a icon.
	 * @return Icon The tile icon.
	 */
	public Icon getIcon() {
		return new ImageIcon(image);
	}
	
	/**
	 * Get the character.
	 * @return char The tile character.
	 */

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public int hashCode() {
		return Arrays.hashCode(image.toString().toCharArray());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass() != o.getClass()) return false;
		Tile t =  (Tile) o;
		return image.equals(t.getImage());
	}
}
