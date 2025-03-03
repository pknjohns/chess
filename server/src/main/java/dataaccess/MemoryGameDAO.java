package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    // key in this case is gameID (int)
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData addGame(GameData game) {
        games.put(game.gameID(), game);
        return game;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void deleteAllGames() {
        games.clear();
    }
}
