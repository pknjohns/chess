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

    public void updateGameWhitePlayer(int gameID, String username) {
        GameData game = games.get(gameID);
        GameData newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        games.remove(gameID);
        addGame(newGame);
    }

    public void updateGameBlackPlayer(int gameID, String username) {
        GameData game = games.get(gameID);
        GameData newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        games.remove(gameID);
        addGame(newGame);
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void deleteAllGames() {
        games.clear();
    }
}
