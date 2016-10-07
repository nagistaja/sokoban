import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class Player {

    int characterRow = 0;
    int characterColumn = 0;

    //paneb mängija alguspunkti paika


    public void setPlayer(int rows, int columns, int[][] levelMap, int tileType) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (levelMap[row][column] == tileType) {
                    characterRow = row;
                    characterColumn = column;

                    //System.out.println(characterRow + " " + characterColumn);
                }
            }
        }
        this.characterRow = characterRow;
        this.characterColumn = characterColumn;
    }
}