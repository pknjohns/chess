package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {
    UserData addUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    Collection<UserData> listUsers() throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
