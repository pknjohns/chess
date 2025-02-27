package dataaccess;

import model.*;
//import org.eclipse.jetty.server.Authentication;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    // key in this case is username (string)
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> tokens = new HashMap<>();

    public UserData addUser(UserData user) {
        users.put(user.username(), user);
        return user;
    }

    public Collection<UserData> listUsers() {
        return users.values();
    }

    public void deleteAllUsers() {
        users.clear();
    }

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

    public AuthData addToken(AuthData token) {
        tokens.put(token.authToken(), token);
        return token;
    }

    public void deleteAllTokens() {
        tokens.clear();
    }
}
