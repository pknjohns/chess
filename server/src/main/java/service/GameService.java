package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.ListGameData;

import java.util.Collection;
import java.util.HashSet;

public class GameService {

    private final AuthDAO authDB;
    private final GameDAO gameDB;

    public GameService(AuthDAO authDB, GameDAO gameDB) {
        this.authDB = authDB;
        this.gameDB = gameDB;
    }

    public Collection<ListGameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData auth = authDB.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else {
            Collection<GameData> games = gameDB.listGames();
            Collection<ListGameData> listedGames = new HashSet<>();
            for (GameData game : games) {
                ListGameData listedGame = new ListGameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
                listedGames.add(listedGame);
            }
            return listedGames;
        }
    }
}
