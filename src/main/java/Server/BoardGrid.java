package Server;

public class BoardGrid {

    private final int ROWS = 6;
    private final int COLS = 9;

    private int[][] matrix = new int[ROWS][COLS];

    public BoardGrid () {
        initializeMatrix();
    }

    public static void main(String[] args) {
        BoardGrid grid = new BoardGrid();
        grid.initializeMatrix();

        grid.printMatrix();
        System.out.println("\n*Player 1 makes move*\n");
        grid.makeMove(3,1);
        grid.printMatrix();

        System.out.println("\n*Player 0 makes move*\n");
        grid.makeMove(3,0);
        grid.printMatrix();
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
     * @param playerID
     */
    public void makeMove(int col, int playerID) {
        for (int i = ROWS-1; i >= 0; i--) {
            if (matrix[i][col] == -1) {
                matrix[i][col] = playerID;
                return;
            }
        }
        System.out.println("Column is full.");
    }

    private void printMatrix() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String symbol = classifySymbol(matrix[i][j]);
                System.out.print(String.format("[%s]", symbol));
            }
            System.out.println();
        }
    }

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

    private void initializeMatrix() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                matrix[i][j] = -1;
            }
        }
    }

}
