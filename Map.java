import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//import java.util.concurrent.TimeUnit;

public class Map{

    public static final int TILE_INVALID = -1;
    private final int TILE_EMPTY = 0;
    private final int TILE_WALL = 1;
    private final int TILE_BOX = 2;
    private final int TILE_TARGET = 3;
    private final int TILE_PLAYER = 4;
    final long timeInterval = 1000;
    final int TILE_WIDTH = 30;
    final int TILE_HEIGHT = 30;
    final int TILE_SEPARATION = 5;
    final int CHARACTER_SIZE = TILE_WIDTH / 2;
    Thread timer;
    private MapLayer walls;
    private MapLayer boxes;
    private MapLayer targets;
    private MapLayer editorMap;
    private Player player;
    private int game = 1;
    private int editor = 2;
    private int interfaceRunning;

    int rows;
    int columns;
    int movesMade = 0;
    int score = 0;
    int goal = 0;
    int runningTime = 0;
    boolean gameRunning = false;

    private MainWindow listener = null;

    public Map (int rows, int columns, int[][] levelMap, int interfaceRunning) {

        this.rows = rows;
        this.columns = columns;
        this.interfaceRunning = interfaceRunning;

        if (interfaceRunning == game){

            walls = new MapLayer();
            walls.setObjects(rows, columns, levelMap, TILE_WALL);
            boxes = new MapLayer();
            boxes.setObjects(rows, columns, levelMap, TILE_BOX);
            targets = new MapLayer();
            targets.setObjects(rows, columns, levelMap, TILE_TARGET);
            player = new Player();
            player.setPlayer(rows, columns, levelMap, TILE_PLAYER);
            goal = getGoal();

        }else if (interfaceRunning == editor) {
            editorMap = new MapLayer();
            editorMap.newMap(rows, columns);
        }
    }

    Runnable timerRunnable = new Runnable() {
        public void run() {
            while (gameRunning) {
                runningTime++;
                listener.onTimerTick(runningTime);
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public void move(int keyCode) {
        boolean validMove = false;

        if (keyCode == KeyEvent.VK_RIGHT) {
            if (walls.getLocationValue(player.characterRow , player.characterColumn + 1) == TILE_EMPTY) {
                if (boxes.getLocationValue(player.characterRow , player.characterColumn + 1) == TILE_EMPTY) {
                    player.characterColumn++;
                    validMove = true;
                } else if (boxes.getLocationValue(player.characterRow , player.characterColumn + 1) == TILE_BOX && boxes.getLocationValue(player.characterRow , player.characterColumn + 2) == TILE_EMPTY && walls.getLocationValue(player.characterRow , player.characterColumn + 2) == TILE_EMPTY) {
                    boxes.changeLocationValue(player.characterRow , player.characterColumn + 1, TILE_EMPTY);
                    boxes.changeLocationValue(player.characterRow , player.characterColumn + 2, TILE_BOX);
                    player.characterColumn++;
                    validMove = true;
                }
            }
        } else if (keyCode == KeyEvent.VK_LEFT) {
            if (walls.getLocationValue(player.characterRow, player.characterColumn - 1) == TILE_EMPTY) {
                if(boxes.getLocationValue(player.characterRow, player.characterColumn - 1) == TILE_EMPTY){
                    player.characterColumn--;
                    validMove = true;
                } else if (boxes.getLocationValue(player.characterRow, player.characterColumn - 1) == TILE_BOX && boxes.getLocationValue(player.characterRow, player.characterColumn - 2) == TILE_EMPTY && walls.getLocationValue(player.characterRow, player.characterColumn - 2) == TILE_EMPTY) {
                    boxes.changeLocationValue(player.characterRow , player.characterColumn - 1, TILE_EMPTY);
                    boxes.changeLocationValue(player.characterRow , player.characterColumn - 2, TILE_BOX);
                    player.characterColumn--;
                    validMove = true;
                }
            }
        } else if (keyCode == KeyEvent.VK_UP) {
            if (walls.getLocationValue(player.characterRow - 1, player.characterColumn) == TILE_EMPTY) {
                if (boxes.getLocationValue(player.characterRow - 1, player.characterColumn) == TILE_EMPTY) {
                    player.characterRow--;
                    validMove = true;
                } else if (boxes.getLocationValue(player.characterRow - 1, player.characterColumn) == TILE_BOX && boxes.getLocationValue(player.characterRow - 2, player.characterColumn) == TILE_EMPTY && walls.getLocationValue(player.characterRow - 2, player.characterColumn) == TILE_EMPTY) {
                    boxes.changeLocationValue(player.characterRow - 1, player.characterColumn, TILE_EMPTY);
                    boxes.changeLocationValue(player.characterRow - 2, player.characterColumn, TILE_BOX);
                    player.characterRow--;
                    validMove = true;
                }
            }
        } else if (keyCode == KeyEvent.VK_DOWN) {
            if (walls.getLocationValue(player.characterRow + 1, player.characterColumn) == TILE_EMPTY) {
                if (boxes.getLocationValue(player.characterRow + 1, player.characterColumn) == TILE_EMPTY) {
                    player.characterRow++;
                    validMove = true;
                } else if (boxes.getLocationValue(player.characterRow + 1, player.characterColumn) == TILE_BOX && boxes.getLocationValue(player.characterRow + 2, player.characterColumn) == TILE_EMPTY && walls.getLocationValue(player.characterRow + 2, player.characterColumn) == TILE_EMPTY) {
                    boxes.changeLocationValue(player.characterRow + 1, player.characterColumn, TILE_EMPTY);
                    boxes.changeLocationValue(player.characterRow + 2, player.characterColumn, TILE_BOX);
                    player.characterRow++;
                    validMove = true;
                }
            }
        }

        if (validMove) {
            movesMade++;
            score = getScore();
            if (listener != null) {
                listener.onMovementsUpdate(movesMade);
                listener.onBoxDelivered(score);
            }
        }

        if (movesMade == 1) {
            gameRunning = true;
            Thread timer = new Thread(timerRunnable);
            timer.start();
        } else if (score == goal) {
            gameRunning = false;
            listener.onLevelEnded(movesMade, runningTime);
            // write score into file
        }
    }

    public int getScore(){
        int scoreCounter = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if(boxes.getLocationValue(row, column) == TILE_BOX && targets.getLocationValue(row, column) == TILE_TARGET){
                    scoreCounter++;
                }
            }
        }
        return scoreCounter;
    }

    public int getGoal(){
        int goalCounter = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if(targets.getLocationValue(row, column) == TILE_TARGET){
                    goalCounter++;
                }
            }
        }
        return goalCounter;
    }

    public void draw(Graphics g, int x, int y) {
        int offsetX = 0;
        int offsetY = 0;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Color color = getTileColor(row, column, interfaceRunning);
                g.setColor(color);
                g.fillRect(x + offsetX, y + offsetY, TILE_WIDTH, TILE_HEIGHT);

                if (interfaceRunning == game &&row == player.characterRow && column == player.characterColumn) {
                    g.setColor(Color.GREEN);
                    int characterX = x + offsetX + (TILE_WIDTH / 2) - (CHARACTER_SIZE / 2);
                    int characterY = y + offsetY + (TILE_HEIGHT / 2) - (CHARACTER_SIZE / 2);
                    g.fillRect(characterX, characterY, CHARACTER_SIZE, CHARACTER_SIZE);
                }

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

    private Color getTileColor (int row, int column, int interfaceRunning) {

        if (interfaceRunning == game) {

            if (walls.getLocationValue(row, column) == TILE_WALL) {
                return Color.GRAY;
            } else if (boxes.getLocationValue(row, column) == TILE_BOX) {
                return Color.ORANGE;
            } else if (targets.getLocationValue(row, column) == TILE_TARGET) {
                return Color.RED;
            } else {
                return Color.BLACK;
            }
        } else if (interfaceRunning == editor) {
            if (editorMap.getLocationValue(row, column) == TILE_WALL) {
                return Color.WHITE;
            } else if (editorMap.getLocationValue(row, column) == TILE_BOX) {
                return Color.ORANGE;
            } else if (editorMap.getLocationValue(row, column) == TILE_TARGET) {
                return Color.RED;
            } else if (editorMap.getLocationValue(row, column) == TILE_PLAYER) {
                return Color.GREEN;
            } else {
                return Color.DARK_GRAY;
            }
        } else {
            return Color.DARK_GRAY;
        }
    }

    public void addGameListener (MainWindow listener) {
        this.listener = listener;
    }


    public int getRow (int y, int rows) {
        int clickRow = -1;
        int offsetY = 0;
        for (int row = 0; row < rows; row++) {
            if ( offsetY < y && y < offsetY + TILE_HEIGHT) {
                clickRow = row;
            }
            offsetY += TILE_HEIGHT + TILE_SEPARATION;
        }
        //System.out.println("clickRow: " + clickRow);
        return clickRow;
    }

    public int getColumn(int x, int columns) {
        int clickColumn = -1;
        int offsetX = 0;
        for (int column = 0; column < columns;  column++) {
            if ( offsetX < x && x < offsetX + TILE_WIDTH) {
                clickColumn = column;
            }
            offsetX += TILE_WIDTH + TILE_SEPARATION;
        }
        //System.out.println("clickColumn: " + clickColumn);
        return clickColumn;
    }


    public void editMap(int x, int y) {
        int row = -1;
        int column = -1;
        if (editorMap != null) {
            row = getRow(y, rows);
            column = getColumn(x, columns);
            boolean valid = row >= 0 && column >= 0;
            if (valid){
                editorMap.changeTile(row, column);
            }
        }
        //System.out.println("map edited");
    }

    public int[] getMapArray1D(int rows, int columns){
        int x = 0;
        int[] arr1D = new int[rows * columns];

        for (int row = 0; row < rows; row++){
            for (int column = 0 ; column < columns; column++) {
                arr1D[x] = editorMap.getLocationValue(row, column);
                x++;
            }
        }
        return arr1D;
    }
}