public class MapLayer {

    private int[][] data;
    private int rows;
    private int columns;

    public void setObjects (int rows, int columns, int[][] levelMap, int tileType) {

        data = new int[rows][columns];

        for (int row = 0;  row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                data[row][column] = 0;
                if (levelMap[row][column] == tileType) {
                    data[row][column] = tileType;
                }
            }
        }
        this.rows = rows;
        this.columns = columns;
    }

    public int getLocationValue (int row, int column) {
        if (row >= 0 && row < rows && column >= 0 && column < columns) {
            int tileValue;
            tileValue = data[row][column];
            return tileValue;
        } else {
            return Map.TILE_INVALID;
        }
    }

    public void changeLocationValue (int row, int column, int newValue) {
        data[row][column] = newValue;
    }

    public void newMap (int rows, int columns) {
        data = new int[rows][columns];

        for (int row = 0;  row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                data[row][column] = 0;
            }
        }
        this.rows = rows;
        this.columns = columns;
    }

    // Need to add - if player excists, change location
    public void changeTile (int row, int column) {
        if (data[row][column] <= 4){
            data[row][column]++;
        } else {
            data[row][column] = 0;
        }
    }
}