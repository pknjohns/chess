package service;

import dataaccess.*;

public class ServiceTestUtil {

    static AuthDAO getAuthDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;

        if (databaseClass.equals(MemoryAuthDAO.class)) {
            db = new MemoryAuthDAO();
        } else {
            db = new MySqlAuthDAO(); // placeholder for when we add the MySqlDatabase
        }

        db.deleteAllAuths();
        return db;
    }

    static GameDAO getGameDataAccess(Class<? extends GameDAO> gDAOclass) throws DataAccessException {
        GameDAO db;

        if (gDAOclass.equals(MemoryGameDAO.class)) {
            db = new MemoryGameDAO();
        } else {
            db = new MySqlGameDAO(); // placeholder for when we add the MySqlDatabase
        }

        db.deleteAllGames();
        return db;
    }

    static UserDAO getUserDataAccess(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO userDB;

        if (daoClass.equals(MemoryUserDAO.class)) {
            userDB = new MemoryUserDAO();
        } else {
            userDB = new MySqlUserDAO(); // placeholder for when we add the MySqlDatabase
        }

        userDB.deleteAllUsers();
        return userDB;
    }

}
