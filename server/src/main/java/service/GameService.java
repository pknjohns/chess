package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.ListGameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class GameService {

    private final AuthDAO authDB;
    private final GameDAO gameDB;
    private int nextID = 1;

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

    public int createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        AuthData auth = authDB.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else {
            GameData game = new GameData(nextID, null, null, gameName, null);
            gameDB.addGame(game);
            return nextID++;
        }
    }

    public void joinGame(String authToken, String teamColor, int gameID) throws AlreadyTakenException, BadRequestException, UnauthorizedException, DataAccessException {
        AuthData auth = authDB.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else {
            if (Objects.equals(teamColor, "WHITE")) {
                if (gameDB.getGame(gameID).whiteUsername() == null) {
                    gameDB.updateGameWhitePlayer(gameID, auth.username());
                } else {
                    throw new AlreadyTakenException("Someone else is already white");
                }
            } else if (Objects.equals(teamColor, "BLACK")) {
                if (gameDB.getGame(gameID).blackUsername() == null) {
                    gameDB.updateGameBlackPlayer(gameID, auth.username());
                } else {
                    throw new AlreadyTakenException("Someone else is already black");
                }
            } else {
                throw new BadRequestException("Invalid team color. Please choose white or black as your team color");
            }
        }
    }
}
