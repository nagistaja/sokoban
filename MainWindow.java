import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;



public class MainWindow extends JFrame implements GameListener, ActionListener, KeyListener, MouseListener {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private JButton setSizeButton;
    private JButton saveButton;
    private JButton highScoreButton;
    private JTextField rowsField;
    private JTextField columnsField;
    private JTextField saveNameField;
    private JLabel rowLabel;
    private JLabel columnLabel;
    private JLabel movesMadeLabel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel saveLabel;
    private DrawingPanel drawingPanel;
    private Map map;
    private String play = "-level";
    private String edit = "-editor";
    public int rows;
    public int columns, i;
    private int[] levelArr1D;
    public int[][] levelMap;
    private int movesMade, boxesDelivered, targets;
    private String rowsString, columnsString;
    boolean isNumber = false;
    private int game = 1;
    private int editor = 2;
    String saveName;
    String scoreFile;
    String playerName = "None";
    private int[][] scoresFromFile;
    private int[] currentLevelScore;
    private int[][] scoresToWrite;
    private int numberOfEntries = 0;
    boolean topTen = false;
    int currentLevelTime, currentLevelMoves;
    boolean runScore;
    int placeInTop;

    public static void main(String[] args) {
        String start = args[0];
        String levelName = "Enter level name...";
        if (start.equals("-level")) {
            levelName = args[1];
        }
        MainWindow mainWindow = new MainWindow(start, levelName);
        mainWindow.setVisible(true);
        System.out.println("Running!");
    }

    public MainWindow(String start, String levelName) {
        if (start.equals(play)) {
            initializeGameGUI();
            initializeEvents();
            loadLevel(levelName);
            initializeMap();
            scoreFile = setScoreFileName(levelName);
        } else if (start.equals(edit)) {
            initializeEditorGUI();
            initializeEditorEvents();
        }
    }

    private void loadLevel (String levelName) {
        int i = 0;
        try{
            FileInputStream inputStream = new FileInputStream(levelName);
            int c;

            while((c= inputStream.read()) != -1){
                if (i == 0) {
                    rows = c;
                    i++;
                } else if (i == 1) {
                    columns = c;
                    levelArr1D = new int[rows * columns];
                    i++;
                } else  {
                    levelArr1D[i - 2] = c;
                    i++;
                }
            }
            if (inputStream != null){
                inputStream.close();
            }
        } catch(IOException ex){
            System.out.println (ex.toString());
            System.out.println("Could not load " + levelName);
        }

        levelMap = new int[rows][columns];
        int j = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                levelMap[row][column] = levelArr1D[j];
                j++;
            }
        }
    }

    private void saveLevel (String name) {
        int i = 0;
        int arrayLenght = rows * columns;
        levelArr1D = new int[arrayLenght];
        levelArr1D = map.getMapArray1D(rows, columns);

        try{
            FileOutputStream outputStream = new FileOutputStream(name);
            int c;
            while(i < arrayLenght + 2){
                if (i == 0) {
                    c = rows;
                    outputStream.write(c);
                    i++;
                } else if (i == 1) {
                    c = columns;
                    outputStream.write(c);
                    i++;
                } else {
                    for (int j = 0; j < arrayLenght; j++) {
                        c = levelArr1D[j];
                        outputStream.write(c);
                        i++;
                    }
                    if (i == arrayLenght + 2){
                        outputStream.close();
                    }
                }
            }
        } catch (IOException ex){
            System.out.println (ex.toString());
            System.out.println("Could not create file");
        }
    }

    private void showHighScore (String scoreFile){
        if (scoreFileExists(scoreFile)) {
            loadScoreFile(scoreFile);
            JOptionPane.showMessageDialog(null, "Top scores: \n " + scoreToText());

        } else {
            JOptionPane.showMessageDialog(null, "No high scores!");
        }
    }

    private String scoreToText (){
        String text = "";
        int entry = 0;
        while (entry < numberOfEntries) {
            text += (entry + 1) + "    ";
            int entryLength = scoresFromFile[entry][0] + 5;
            for (int i = 0; i < entryLength - 5; i++) {
                text += (char)(scoresFromFile[entry][i + 1]);
            }
            text += "  Moves: ";
            byte[] bytes = new byte [2];
            bytes[0] = (byte)(scoresFromFile[entry][entryLength - 4]);
            bytes[1] = (byte)(scoresFromFile[entry][entryLength - 3]);
            text += bytesToInt(bytes);
            text += "  Time: ";
            bytes[0] = (byte)(scoresFromFile[entry][entryLength - 2]);
            bytes[1] = (byte)(scoresFromFile[entry][entryLength - 1]);
            text += bytesToInt(bytes);
            text += "\n";
            entry++;
        }
        return text;
    }

    private void loadScoreFile (String scoreFile) {
        int i = 0;
        try {
            FileInputStream inputStream = new FileInputStream(scoreFile);
            int c, nameLenght;
            int totalLength = 0;
            int entry = 0;
            c = inputStream.read();
            numberOfEntries = c;
            scoresFromFile = new int[numberOfEntries][];

            while(entry < numberOfEntries && entry < 10){
                c = inputStream.read();
                nameLenght = c;
                scoresFromFile[entry] = new int [nameLenght + 5];
                scoresFromFile[entry][0] = nameLenght;
                for (int j = 1; j < nameLenght + 5; j++) {
                    c = inputStream.read();
                    scoresFromFile[entry][j] = c;
                }
                entry++;
            }
            if (inputStream != null){
                inputStream.close();
            }
        } catch(IOException ex){
            System.out.println (ex.toString());
        }
    }

    private void writeScoreFile () {
        int i = 0;

        try{
            FileOutputStream outputStream = new FileOutputStream(scoreFile);
            int c, entryLength;
            int writingEntry = 0;

            if(numberOfEntries == 1) {
                entryLength = playerName.length() + 6;
                byte [] scoreBytes = new byte [2];
                byte [] timeBytes = new byte [2];
                scoreBytes = intToBytes(currentLevelMoves);
                timeBytes = intToBytes(currentLevelTime);
                for (i = 0; i < entryLength; i++) {
                    if (i == 0) {
                        c = 1;
                        outputStream.write(c);
                    } else if (i == 1) {
                        c = entryLength - 6;
                        outputStream.write(c);
                    } else if (i > 1 && i < entryLength -4) {
                        c = (int)(playerName.charAt(i-2));
                        outputStream.write(c);
                    } else if (i >= entryLength -4 && i < entryLength - 2) {
                        c = scoreBytes[i - entryLength + 4];
                        outputStream.write(c);
                    } else {
                        c = timeBytes[i - entryLength + 2];
                        outputStream.write(c);
                    }
                }
                outputStream.close();
            } else {
                c = numberOfEntries;
                outputStream.write(c);
                while (writingEntry < numberOfEntries && writingEntry < 10){
                    c = scoresToWrite[writingEntry][0];
                    outputStream.write(c);
                    entryLength = c + 4;
                    for (i = 0; i < entryLength; i++) {
                        c = scoresToWrite[writingEntry][i + 1];
                        outputStream.write(c);
                    }
                    writingEntry++;
                }
                outputStream.close();
            }

        } catch (IOException ex){
            System.out.println (ex.toString());
            System.out.println("Could not create file");
        }
    }

    private String setScoreFileName (String levelName) {
        String tempName = "";
        if (levelName.length() > 4) {
            tempName = levelName.substring(0, levelName.length()-4);
            tempName += ".score";
        }
        return tempName;
    }

    private boolean scoreFileExists (String scoreFile) {
        boolean exists;
        File f = new File("./" + scoreFile);
        if (f.isFile()) {
            exists = true;
        } else {
            exists = false;
        }
        return exists;
    }

    private byte[] intToBytes(int x){
        return new byte[] { (byte)(x >> 8), (byte)x };
    }

    private int bytesToInt (byte[] bytes){
        return bytes[0] << 8 | (bytes[1] & 0xFF);
    }

    private void checkHighScore(){
        int toCompare, entryLength;
        int entry = numberOfEntries - 1;
        byte[] bytes = new byte[2];
        placeInTop = numberOfEntries;
        while (entry >= 0) {
            entryLength = scoresFromFile[entry][0] + 5;
            bytes[0] = (byte)(scoresFromFile[entry][entryLength - 4]);
            bytes[1] = (byte)(scoresFromFile[entry][entryLength - 3]);
            toCompare = bytesToInt(bytes);
            if(toCompare > currentLevelMoves){
                placeInTop = entry;
            } else if (toCompare == currentLevelMoves) {
                bytes[0] = (byte)(scoresFromFile[entry][entryLength - 2]);
                bytes[1] = (byte)(scoresFromFile[entry][entryLength - 1]);
                int timeToCompare = bytesToInt(bytes);
                if (timeToCompare > currentLevelTime) {
                    placeInTop = entry;
                }
            }
            entry--;
        }
    }

    private void setNewScores (){
        int entryLength = playerName.length() + 5;
        currentLevelScore = new int[entryLength];
        byte [] scoreBytes = new byte [2];
        byte [] timeBytes = new byte [2];
        scoreBytes = intToBytes(currentLevelMoves);
        timeBytes = intToBytes(currentLevelTime);
        for (i = 0; i < entryLength; i++) {
            if (i == 0) {
                currentLevelScore[i] = entryLength - 5;
            } else if (i > 0 && i < entryLength -4) {
                currentLevelScore[i] = (int)(playerName.charAt(i-1));
            } else if (i >= entryLength -4 && i < entryLength - 2) {
                currentLevelScore[i] = scoreBytes[i - entryLength + 4];
            } else {
                currentLevelScore[i] = timeBytes[i - entryLength + 2];
            }
        }
        if (placeInTop < 10) {
            scoresToWrite = new int[numberOfEntries][];
        } else {
            scoresToWrite = new int[10][];
        }

        int entry = 0;

        while (entry < numberOfEntries && entry < 10) {
            if (entry < placeInTop) {
                scoresToWrite[entry] = scoresFromFile[entry];
            } else if (entry == placeInTop) {
                scoresToWrite[entry] = currentLevelScore;
            } else if (entry > placeInTop){
                scoresToWrite[entry] = scoresFromFile[entry - 1];
            }
            entry++;
        }
    }

    private void initializeGameGUI() {
        setTitle("Sokoban");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        //infoPanel.setBackground(Color.GRAY);
        movesMadeLabel = new JLabel();
        movesMadeLabel.setText("Movements:");
        movesMadeLabel.setPreferredSize(new Dimension(150, 25));
        infoPanel.add(movesMadeLabel);
        scoreLabel = new JLabel();
        scoreLabel.setText("Boxes delivered:");
        scoreLabel.setPreferredSize(new Dimension(150, 25));
        infoPanel.add(scoreLabel);
        timeLabel = new JLabel();
        timeLabel.setText("Time: " );
        timeLabel.setPreferredSize(new Dimension(150, 25));
        infoPanel.add(timeLabel);
        add(infoPanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel();
        highScoreButton = new JButton();
        highScoreButton.setText("Show high scores");
        highScoreButton.setFocusable(false);
        buttonsPanel.add(highScoreButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initializeEditorGUI() {
        setTitle("Sokoban editor");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        drawingPanel = new DrawingPanel();
        add(drawingPanel, BorderLayout.CENTER);

        JPanel editorPanel = new JPanel();
        rowLabel = new JLabel("Rows: ");
        editorPanel.add(rowLabel);
        rowsField = new JTextField();
        rowsField.setPreferredSize(new Dimension(50, 25));
        editorPanel.add(rowsField);
        columnLabel = new JLabel("Columns: ");
        editorPanel.add(columnLabel);
        columnsField = new JTextField();
        columnsField.setPreferredSize( new Dimension(50, 25));
        editorPanel.add(columnsField);
        setSizeButton = new JButton();
        setSizeButton.setText("Set board Size");
        editorPanel.add(setSizeButton);
        saveLabel = new JLabel("Level name: ");
        editorPanel.add(saveLabel);
        saveNameField = new JTextField();
        saveNameField.setPreferredSize( new Dimension(100, 25));
        editorPanel.add(saveNameField);
        saveButton = new JButton();
        saveButton.setText("Save");
        editorPanel.add(saveButton);
        add(editorPanel, BorderLayout.SOUTH);
    }

    private void initializeEvents() {
        addKeyListener(this);
        setFocusable(true); // in order to make KeyListener work!
        highScoreButton.addActionListener(this);
        runScore = true;
    }

    private void initializeEditorEvents() {
        saveButton.addActionListener(this);
        setSizeButton.addActionListener(this);
        drawingPanel.addMouseListener(this);
        addKeyListener(this);
        setFocusable(true); // in order to make KeyListener work!
    }

    private void initializeMap() {
        map = new Map(rows, columns, levelMap, game);
        map.addGameListener(this);
        drawingPanel.setMap(map);
    }

    private void initializeEditor() {
        int[][] level = new int[rows][columns];
        map = new Map(rows, columns, levelMap, editor);

        drawingPanel.setMap(map);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            System.out.println("Save Pressed!");

            saveName = saveNameField.getText() + ".skb";
            System.out.println("Name = " + saveName);
            saveLevel(saveName);
        } else if (e.getSource() == setSizeButton) {
            rowsString = rowsField.getText();
            columnsString = columnsField.getText();
            if (isNumber(rowsString, columnsString)) {
                rows = Integer.parseInt(rowsString);
                columns = Integer.parseInt(columnsString);
            }
            initializeEditor();
            drawingPanel.repaint();
        } else if (e.getSource() == highScoreButton) {
            showHighScore(scoreFile);
            drawingPanel.repaint();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (map != null) {
            map.move(e.getKeyCode());
            drawingPanel.repaint();
        }
    }

    public void keyPressed(KeyEvent e) { }

    public void keyTyped(KeyEvent e) { }


    public void onMovementsUpdate (int movementsMade){
        movesMadeLabel.setText("Movements: " + movementsMade);
    }

    public void onLevelEnded (int movementsMade, int levelTime){
        if (runScore){
            currentLevelMoves = movementsMade;
            currentLevelTime = levelTime;
            if (scoreFileExists(scoreFile)){
                loadScoreFile(scoreFile);
                checkHighScore();
                if (placeInTop <= 10) {
                    playerName = (String)JOptionPane.showInputDialog(null,"Level finished in " + movementsMade + " moves and " + levelTime + " seconds! \n Result is no" + (placeInTop + 1) + " in Top! \n Enter your name:" );
                    if (playerName.length() < 1) {
                        playerName = "Unknown";
                    }
                    numberOfEntries++;
                    if (numberOfEntries > 10) {
                        numberOfEntries = 10;
                    }
                    setNewScores();
                    writeScoreFile();
                } else {
                    JOptionPane.showMessageDialog(null, "Level finished in " + movementsMade + " moves and " + levelTime + " seconds!");
                }
                runScore = false;
            } else {
                playerName = (String)JOptionPane.showInputDialog(null, "Level finished in " + movementsMade + " moves and " + levelTime + " seconds! \n Mew high score! \n Enter your name:" );
                numberOfEntries = 1;
                writeScoreFile();
                runScore = false;
            }
        }
    }

    public void onBoxDelivered (int boxesDelivered){
        scoreLabel.setText("Boxes delivered: " + boxesDelivered);
    }

    public void onTimerTick (int timerTick) {
        timeLabel.setText("Time: " + timerTick);
    }

    public boolean isNumber(String rows, String columns) {
        if (rows != null && columns != null){
            try {
                int test;
                test = Integer.parseInt(rowsString);
                test = Integer.parseInt(columnsString);
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Size must be a number!");
                return false;
            }
        } else {
            return false;
        }
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) {
        int clickX = e.getX() - drawingPanel.mapOffsetX();
        int clickY = e.getY() - drawingPanel.mapOffsetY();
        //System.out.println( e.getX() + ", " + e.getY() );
        if (map != null) {
            map.editMap(clickX, clickY);
        }
        repaint();
    }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

}