package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    UserData addUser(UserData user) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;

    GameData addGame(GameData game) throws DataAccessException;

    void deleteAllGames() throws DataAccessException;

    void deleteAllTokens() throws DataAccessException;
}
