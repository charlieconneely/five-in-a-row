import Server.BoardGrid;
import Server.GameManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BoardGridTest {

    private BoardGrid boardGrid;
    @Mock
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        boardGrid = new BoardGrid(gameManager);
        // Initialise mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("'Column is full' message displays")
    void makeMoveOnFullColumn() {
        boardGrid.initializeMatrix();
    }

    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource({
            "-1, ' '",
            "0, 'x'",
            "1, 'o'"
    })
    void assignCharacter(int input, String expectedResult) {
        BoardGrid boardGrid = new BoardGrid(new GameManager());
        assertEquals(expectedResult, boardGrid.classifySymbol(input),
                () -> input  + "should equal " + expectedResult);
    }

}
