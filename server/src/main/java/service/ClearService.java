package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ClearService {

    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearDB() throws DataAccessException {
        dataAccess.deleteAllUsers();
        dataAccess.deleteAllGames();
        dataAccess.deleteAllTokens();
    }
}