package Server;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class GameManager {

    List<String> players = new ArrayList<>();
    // represents which players turn it is - players[0] or players[1]
    private int playerTurn = 0;

    private BoardGrid boardGrid;

    public GameManager() {
        boardGrid = new BoardGrid();
    }

    public void addPlayer(String playerName) {
        players.add(playerName);
    }

    public String getPlayers() {
        String allPlayers = "";
        for (String p : players) {
            allPlayers += p + " ";
        }
        return allPlayers;
    }

    public void handlePlayerMove(int column) {
        boardGrid.makeMove(column, playerTurn);
        switchPlayerTurn();
    }

    public String getBoardStateAsText() {
        return boardGrid.getGridAsText();
    }

    public String getPlayerTurn() {
        return players.get(playerTurn);
    }

    public int numberOfPlayers() {
        return players.size();
    }

    private void switchPlayerTurn() {
        if (playerTurn == 0) {
            playerTurn = 1;
        } else {
            playerTurn = 0;
        }
    }
}
