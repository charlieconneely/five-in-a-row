package Server;

/**
 * Stores the game board as a 2d array.
 * Handles all operations such as adding a disc and searching for winning lines.
 */
public class BoardGrid {

    private final int WIN_LENGTH = 5;
    private final int ROWS = 6;
    private final int COLS = 9;

    private GameManager gameManager;
    private int[][] matrix = new int[ROWS][COLS];
    private int playerID;

    public BoardGrid (GameManager gameManager) {
        this.gameManager = gameManager;
        initializeMatrix();
    }

    /**
     * Returns the grid matrix as a String object to be sent to a Client.
     */
    public String getGridAsText() {
        String fullGrid = "";
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String symbol = classifySymbol(matrix[i][j]);
                fullGrid += String.format("[%s]", symbol);
            }
            fullGrid += "\n";
        }
        return fullGrid;
    }

    /**
     * Loop from bottom row upward until free coordinate is found.
     * Assign coordinate with value passed into function.
     *
     * @param col Column chosen by player
     * @param playerID (0 or 1)
     */
    public void makeMove(int col, int playerID) {
        this.playerID = playerID;
        for (int row = ROWS-1; row >= 0; row--) {
            if (matrix[row][col] == -1) {
                matrix[row][col] = playerID;
                checkForWinningLine(row, col);
                return;
            }
        }
        System.out.println("Column is full.");
    }

    /**
     * Initialize each position with default value
     * (-1 printed as empty string)
     */
    public void initializeMatrix() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                matrix[i][j] = -1;
            }
        }
    }

    public int getValueAtPosition(int row, int col) {
        return matrix[row][col];
    }

    /**
     * Takes in player ID (0 or 1) or default -1 and returns
     * the respective character to display on console.
     *
     * @param number player ID
     * @return String 'x', 'o', or ' '.
     */
    private String classifySymbol(int number) {
        switch (number) {
            case 0:
                return "x";
            case 1:
                return "o";
            default:
                return " ";
        }
    }

    private void checkForWinningLine(int row, int col) {
        searchHorizontally(row);
        searchVertically(row, col);
        searchDiagonally(row, col);
    }

    /**
     * Check if we are high enough on the board for a vertical line of 5 to exist.
     * Count the consecutive positions with value as playerID
     */
    private void searchVertically(int row, int col) {
        int counter = 0;
        if (row > 1) return;
        for (int i = row; i < (row + WIN_LENGTH); i++) {
            if (matrix[i][col] == playerID) {
                counter++;
            } else {
                return;
            }
        }
        if (counter == WIN_LENGTH) gameManager.setWinner(playerID);
    }

    /**
     * Search horizontally at row n for five consecutive positions
     * containing playerID.
     */
    private void searchHorizontally(int row) {
        int counter = 0;
        for (int i = 0; i < COLS; i++) {
            if (matrix[row][i] == playerID) {
                counter++;
            } else {
                counter = 0;
            }
            if (counter == WIN_LENGTH) gameManager.setWinner(playerID);
        }
    }

    private void searchDiagonally(int row, int col) {
        if (winningDiagLineImpossible(row, col)) return;
        searchAscending();
        searchDescending();
    }

    /**
     * Traverse grid from left to middle, searching each ascending diagonal line.
     */
    private void searchAscending() {
        searchAscLine(ROWS-2, 0);
        for (int i = 0; i < WIN_LENGTH; i++) {
            searchAscLine(ROWS-1, i);
        }
    }

    /**
     * Traverse grid from right to middle, searching each descending diagonal line.
     */
    private void searchDescending() {
        searchDescLine(ROWS-2, 8);
        for (int col = COLS-1; col > WIN_LENGTH-1; col--) {
            searchDescLine(ROWS-1, col);
        }
    }

    /**
     * Search individual ascending diagonal lines.
     *
     * @param startingRow Row to start from.
     * @param startingCol Column to start from.
     */
    private void searchAscLine(int startingRow, int startingCol) {
        int counter = 0;
        int r = startingRow;
        for (int c = startingCol; c < (startingCol + (WIN_LENGTH+1)); c++) {
            if (outOfBounds(r, c)) continue;
            if (matrix[r][c] == playerID) {
                counter++;
            } else {
                counter = 0;
            }
            if (counter == WIN_LENGTH) gameManager.setWinner(playerID);
            r--;
        }
    }

    /**
     * Search individual descending diagonal lines.
     *
     * @param startingRow Row to start from.
     * @param startingCol Column to start from.
     */
    private void searchDescLine(int startingRow, int startingCol) {
        int counter = 0;
        int r = startingRow;
        for (int c = startingCol; c > (startingCol - (WIN_LENGTH+1)); c--) {
            if (outOfBounds(r, c)) continue;
            if (matrix[r][c] == playerID) {
                counter++;
            } else {
                counter = 0;
            }
            if (counter == WIN_LENGTH) gameManager.setWinner(playerID);
            r--;
        }
    }

    private boolean outOfBounds(int row, int col) {
        return (row < 0 || row > ROWS-1 || col < 0 || col > COLS-1);
    }

    /**
     * Checks if position (row, col) is one of the 4 positions at which
     * a winning diagonal line of five cannot exist.
     */
    private boolean winningDiagLineImpossible(int row, int col) {
        return ((row == 2 || row == 3) && (col == 0 || col == 8));
    }
}
