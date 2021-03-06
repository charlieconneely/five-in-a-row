package Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Communicates the game state with the WebServer class.
 * Manages the game (stores names, whose turn it is, who quit, who won etc.)
 */
public class GameManager {

    List<String> players = new ArrayList<>();
    // represents which players turn it is - players[0] or players[1]
    private int playerTurn = 0;
    private String winner = "";
    private BoardGrid boardGrid = new BoardGrid(this);

    public GameManager() {
        setBoardGrid(boardGrid);
    }

    /**
     * Returns a string containing all player names in the list.
     *
     * @return String all players
     */
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

    private void switchPlayerTurn() {
        if (playerTurn == 0) {
            playerTurn = 1;
        } else {
            playerTurn = 0;
        }
    }

    public void addPlayer(String playerName) {
        players.add(playerName);
    }

    public void removePlayer(String playerName) {
        players.remove(playerName);
        // reset board.
        boardGrid.initializeMatrix();
        if (players.size() == 1) playerTurn = 0;
    }

    public void setBoardGrid(BoardGrid grid) {
        this.boardGrid = grid;
    }

    public void setWinner(int id) {
        this.winner = players.get(id);
    }

    public String getWinner() {
        return this.winner;
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
}
