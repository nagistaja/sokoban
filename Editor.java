import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Editor extends JPanel {

    //enter size of the map +
    //create empty array  map array
    //listen to clicks and create map
    //convert to 2D
    //file chooser window
    //write bytes to file

    private final int TILE_EMPTY = 0;
    private final int TILE_NOT_EMPTY = 1;
    private final int TILE_WALL = 1;
    private final int TILE_BOX = 2;
    private final int TILE_TARGET = 3;
    private final int TILE_PLAYER = 4;
    final int TILE_WIDTH = 30;
    final int TILE_HEIGHT = 30;
    final int TILE_SEPARATION = 5;
    private Color drawingColor = Color.BLACK;

    private MapLayer map;

    int rows, columns;

    public Editor(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        map = new MapLayer();
        map.newMap(rows, columns);
        System.out.println("Editor started");

    }


    public void draw(Graphics g, int x, int y) {
        int offsetX = 0;
        int offsetY = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Color color = getTileColor(row, column);
                g.setColor(color);
                g.fillRect(x + offsetX, y + offsetY, TILE_WIDTH, TILE_HEIGHT);

                offsetX += TILE_WIDTH + TILE_SEPARATION;
            }
            offsetX = 0;
            offsetY += TILE_HEIGHT + TILE_SEPARATION;
        }
    }

    public int getWidth() {
        return (columns * TILE_WIDTH) + ((columns - 1) * TILE_SEPARATION);
    }

    public int getHeight() {
        return (rows * TILE_HEIGHT) + ((rows - 1) * TILE_SEPARATION);
    }

    private Color getTileColor (int row, int column) {
        if (map.getLocationValue(row, column) == TILE_WALL) {
            //System.out.println("wall");
            return Color.GRAY;
        } else if (map.getLocationValue(row, column) == TILE_BOX) {
            //System.out.println("box");
            return Color.ORANGE;
        } else if (map.getLocationValue(row, column) == TILE_TARGET) {
            //System.out.println("Target");
            return Color.RED;
        } else if (map.getLocationValue(row, column) == TILE_PLAYER) {
            //System.out.println("Target");
            return Color.GREEN;
        } else {
            System.out.println("Empty");
            return Color.WHITE;
        }
    }

	/*public void startEditor( Editor editor) {
		this.editor = editor;
	}*/

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);
        //g.fillRect(0, 0, MainWindow.WINDOW_WIDTH, MainWindow.WINDOW_HEIGHT);

        int x = (getSize().width / 2) - (getWidth() / 2);
        int y = (getSize().height / 2) - (getHeight() / 2);
        draw(g, x, y);

        System.out.println("paint called!");
    }

	/*public void updateColor(Color newColor) {
		drawingColor = newColor;
		repaint();
	}*/


}