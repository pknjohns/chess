package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData addGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteAllGames() throws DataAccessException;

}
