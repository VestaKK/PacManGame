package matachi.mapeditor.editor;

import matachi.mapeditor.editor.Tile;
import matachi.mapeditor.editor.TileManager;
import matachi.mapeditor.grid.Grid;
import matachi.mapeditor.grid.GridModel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * GridModelLoader is a commander that provides global functionality to load and save map files,
 * using the character -> tileName encoding provided by TileManager
 */
public class GridModelLoader {

    /**
     * Generates a grid model based on the given file
     * @param mapFile
     * @return
     */
    public static Grid createModel(File mapFile) {

        Document document = buildDocument(mapFile);
        Element rootNode = document.getRootElement();
        List sizeList = rootNode.getChildren("size");
        Element sizeElem = (Element) sizeList.get(0);
        int height = Integer.parseInt(sizeElem.getChildText("height"));
        int width = Integer.parseInt(sizeElem.getChildText("width"));

        GridModel output = new GridModel(width, height, TileManager.getInstance().getDefaultChar());

        List rows = rootNode.getChildren("row");
        for (int y = 0; y < rows.size(); y++) {
            Element cellsElem = (Element) rows.get(y);
            List cells = cellsElem.getChildren("cell");

            for (int x = 0; x < cells.size(); x++) {
                Element cell = (Element) cells.get(x);
                String cellValue = cell.getText();

                for (Tile tile : TileManager.getInstance().getTiles()) {
                    if (tile.getTileName().equalsIgnoreCase(cellValue)) {
                        output.setTile(x, y, tile.getCharacter());
                        break;
                    }
                }
            }
        }
        return output;
    }

    /**
     * Saves a file based on the provided model
     * @param selectedFile
     * @param gridModel
     */
    public static void saveModel(File selectedFile, Grid gridModel) {
        Element level = new Element("level");
        Document doc = new Document(level);
        doc.setRootElement(level);

        Element size = new Element("size");
        int height = gridModel.getHeight();
        int width = gridModel.getWidth();
        size.addContent(new Element("width").setText(width + ""));
        size.addContent(new Element("height").setText(height + ""));
        doc.getRootElement().addContent(size);

        for (int y = 0; y < height; y++) {
            Element row = new Element("row");
            for (int x = 0; x < width; x++) {
                char tileChar = gridModel.getTile(x,y);

                String type = TileManager
                        .getInstance()
                        .getTile(tileChar)
                        .getTileName();

                Element e = new Element("cell");
                row.addContent(e.setText(type));
            }
            doc.getRootElement().addContent(row);
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        try {
            xmlOutput.output(doc, new FileWriter(selectedFile));
        } catch (IOException e) {
        }
    }

    /**
     * Internal Document creation function
     * @param mapFile
     * @return
     */
    private static Document buildDocument(File mapFile) {
        if (!mapFile.canRead() || !mapFile.exists())
            return null;

        SAXBuilder builder = new SAXBuilder();

        try {
            return (Document) builder.build(mapFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
