package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.ListGameData;
import facade.BadRequestException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    public int createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
        AuthData auth = authDB.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else {
            GameData game = new GameData(1, null, null, gameName, null);
            //CreateRequest cReq = new CreateRequest(gameName);
            return gameDB.addGame(game);
            //return gameDB.addGame(cReq);
        }
    }

    public void joinGame(String authToken, String teamColor, int gameID)
            throws AlreadyTakenException,
            BadRequestException,
            UnauthorizedException,
            DataAccessException {

        AuthData auth = authDB.getAuth(authToken);
        GameData game = gameDB.getGame(gameID);
        if (auth == null) {
            throw new UnauthorizedException("Bad token");
        } else if (gameID < 1 || game == null) {
            throw new BadRequestException("Invalid gameID");
        } else {
            if (Objects.equals(teamColor, "WHITE")) {
                if (game.whiteUsername() == null) {
                    gameDB.updateGameWhitePlayer(gameID, auth.username());
                } else {
                    throw new AlreadyTakenException("Someone else is already the white player");
                }
            } else if (Objects.equals(teamColor, "BLACK")) {
                if (game.blackUsername() == null) {
                    gameDB.updateGameBlackPlayer(gameID, auth.username());
                } else {
                    throw new AlreadyTakenException("Someone else is already the black player");
                }
            } else {
                throw new BadRequestException("Invalid team color. Please choose white or black as your team color");
            }
        }
    }
}
