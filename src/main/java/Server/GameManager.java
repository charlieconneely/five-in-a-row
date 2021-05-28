package Server;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    List<String> players = new ArrayList<>();
    // represents which players turn it is - players[0] or players[1]
    private int playerTurn = 0;

    public GameManager() {
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

    public String getPlayerTurn() {
        return players.get(playerTurn);
    }

    public int numberOfPlayers() {
        return players.size();
    }

}
