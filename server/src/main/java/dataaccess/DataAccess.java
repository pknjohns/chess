package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    UserData addUser(UserData user) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;

    GameData addGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteAllGames() throws DataAccessException;

    AuthData addToken(AuthData token) throws DataAccessException;

    void deleteAllTokens() throws DataAccessException;
}
