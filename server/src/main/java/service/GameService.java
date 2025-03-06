package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final AuthDAO authDB;
    private final GameDAO gameDB;

    public GameService(AuthDAO authDB, GameDAO gameDB) {
        this.authDB = authDB;
        this.gameDB = gameDB;
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData auth = authDB.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else {
            return gameDB.listGames();
        }
    }
}
