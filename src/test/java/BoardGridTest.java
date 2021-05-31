import Server.BoardGrid;
import Server.GameManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Allows us to mock classes
@ExtendWith(MockitoExtension.class)
public class BoardGridTest {

    @Mock private GameManager mockGameManager;
    private BoardGrid boardGrid;

    @BeforeEach
    void setUp() {
        boardGrid = new BoardGrid(mockGameManager);
    }

    @Test
    @DisplayName("Correct positions in matrix are populated when players chooses column")
    void makeMove() {
        int column = 8;
        int playerID = 0;
        // when
        boardGrid.makeMove(column, playerID);
        // then
        assertEquals(playerID, boardGrid.getValueAtPosition(5, column));

        // make another move on the same position
        boardGrid.makeMove(column, playerID);
        // assert that the row above is now populated with val playerID
        assertEquals(playerID, boardGrid.getValueAtPosition(4, column));
    }

    @Test
    @DisplayName("setWinner is called with playerID 1 when five hor o's exist.")
    void makeFiveMovesOnSameColumn() {
        int column = 2;
        int playerID = 1;
        // when
        for (int i = 0; i < 5; i++) {
            boardGrid.makeMove(column, playerID);
        }
        // then
        verify(mockGameManager).setWinner(playerID);
    }

    @Test
    @DisplayName("setWinner is called with playerID 0 when five vert x's exist.")
    void makeFiveMovesOnSameRow() {
        int playerID = 0;
        // when
        for (int col = 0; col < 5; col++) {
            boardGrid.makeMove(col, playerID);
        }
        // then
        verify(mockGameManager).setWinner(playerID);
    }

    @Test
    @DisplayName("setWinner is called with playerID 0 when five ascending x's exist.")
    void makeAscendingLine() {
        int playerID = 0;
        int startingCol = 1;
        int count = 1;
        // when
        // Create the stairs for a diagonal line of value playerID
        for (int col = startingCol; col < (startingCol+4); col++) {
            for (int i = 0; i < count; i++) {
                boardGrid.makeMove(col, 1);
            }
            count++;
        }
        // Create diagonal line
        for (int col = 0; col < 5; col++) {
            boardGrid.makeMove(col, playerID);
        }
        // then
        verify(mockGameManager).setWinner(playerID);
    }

    @Test
    @DisplayName("setWinner is called with playerID 0 when five descending x's exist.")
    void makeDescendingLine() {
        int playerID = 0;
        int startingCol = 7;
        int count = 1;
        // when
        // Create the stairs for a diagonal line of value playerID
        for (int col = startingCol; col > (startingCol-4); col--) {
            for (int i = 0; i < count; i++) {
                boardGrid.makeMove(col, 1);
            }
            count++;
        }
        // Create diagonal line
        for (int col = 8; col > 3; col--) {
            boardGrid.makeMove(col, playerID);
        }
        //then
        verify(mockGameManager).setWinner(playerID);
    }
}
