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
public class GameManagerTest {

    @Mock private BoardGrid grid;
    private GameManager gameManager;
    private String fakeName1, fakeName2;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
        gameManager.setBoardGrid(grid);
        fakeName1 = "Player 1";
        fakeName2 = "Player 2";
        gameManager.addPlayer(fakeName1);
        gameManager.addPlayer(fakeName2);
    }

    @Test
    @DisplayName("getPlayers returns a String containing all player names.")
    void getPlayersAsString() {
        String bothNames = "Player 1 Player 2 ";
        assertEquals(bothNames, gameManager.getPlayers());
    }

    @Test
    @DisplayName("handlePlayerMove should call grid.makeMove and switchTurn.")
    void handlesPlayerMove() {
        int column = 2;
        int initialPlayerTurn = 0;
        gameManager.handlePlayerMove(column);
        verify(grid).makeMove(column, initialPlayerTurn);
        assertEquals(fakeName2, gameManager.getPlayerTurn());
    }

    @Test
    @DisplayName("removePlayer calls grid.initialiseMatrix and set playerTurn to 0")
    void checkRemovePlayer() {
        gameManager.removePlayer(fakeName2);
        // players list size should now be 1
        assertEquals(1, gameManager.numberOfPlayers());
        verify(grid).initializeMatrix();
        assertEquals(fakeName1, gameManager.getPlayerTurn());
    }
}
