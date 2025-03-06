package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {

    AuthData getAuth(String token) throws  DataAccessException;

    AuthData addAuth(AuthData token) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    Collection<AuthData> listAuths() throws DataAccessException;

    void deleteAllAuths() throws DataAccessException;
}
