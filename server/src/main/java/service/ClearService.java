package service;

import dataaccess.*;

public class ClearService {

    private final AuthDAO authDB;
    private final GameDAO gameDB;
    private final UserDAO userDB;

    public ClearService(AuthDAO authDB, GameDAO gameDB, UserDAO userDB) {
        this.authDB = authDB;
        this.gameDB = gameDB;
        this.userDB = userDB;
    }

    public void clearDB() throws DataAccessException {
        userDB.deleteAllUsers();
        gameDB.deleteAllGames();
        authDB.deleteAllAuths();
    }
}