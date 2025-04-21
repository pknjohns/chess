package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Integer addGame(GameData game) throws DataAccessException;

    void updateGameWhitePlayer(int gameID, String username) throws DataAccessException;

    void updateGameBlackPlayer(int gameID, String username) throws DataAccessException;

    void updateGame(int gameID, ChessGame game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteAllGames() throws DataAccessException;

}
