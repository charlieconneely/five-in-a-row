import Server.BoardGrid;
import Server.GameManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BoardGridTest {

    @Test
    @DisplayName("0 = 'x'")
    void assignsCorrectCharacter() {
        BoardGrid boardGrid = new BoardGrid(new GameManager());
        assertEquals("x", boardGrid.classifySymbol(0), "0 should return 'x'");
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
