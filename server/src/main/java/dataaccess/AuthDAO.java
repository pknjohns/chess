package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    AuthData addToken(AuthData token) throws DataAccessException;

    Collection<AuthData> listTokens() throws DataAccessException;

    void deleteAllTokens() throws DataAccessException;
}
