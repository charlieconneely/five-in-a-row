package Server;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    List<String> players = new ArrayList<String>();

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

    public int numberOfPlayers() {
        return players.size();
    }

}
