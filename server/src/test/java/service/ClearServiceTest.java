package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private AuthDAO getAuthDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;

        if (databaseClass.equals(MemoryAuthDAO.class)) {
            db = new MemoryAuthDAO();
        } else {
            db = new MySqlAuthDAO(); // placeholder for when we add the MySqlDatabase
        }

        db.deleteAllAuths();
        return db;
    }

    private GameDAO getGameDataAccess(Class<? extends GameDAO> gDAOclass) throws DataAccessException {
        GameDAO db;

        if (gDAOclass.equals(MemoryGameDAO.class)) {
            db = new MemoryGameDAO();
        } else {
            db = new MySqlGameDAO(); // placeholder for when we add the MySqlDatabase
        }

        db.deleteAllGames();
        return db;
    }

    private UserDAO getUserDataAccess(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO userDB;

        if (daoClass.equals(MemoryUserDAO.class)) {
            userDB = new MemoryUserDAO();
        } else {
            userDB = new MySqlUserDAO(); // placeholder for when we add the MySqlDatabase
        }

        userDB.deleteAllUsers();
        return userDB;
    }

    static Stream<Arguments> provideClasses() {
        return Stream.of(
                Arguments.of(MySqlAuthDAO.class, MySqlGameDAO.class, MySqlUserDAO.class),
                Arguments.of(MemoryAuthDAO.class, MemoryGameDAO.class, MemoryUserDAO.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provideClasses")
    void doClearDB(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        // Sample users
        var user1 = new UserData("pk", "1234", "pk@cs.com");
        var user2 = new UserData("kk", "5678", "kk@cs.com");
        var user3 = new UserData("pp", "1234", "cs.com");

        userDB.addUser(user1);
        userDB.addUser(user2);
        userDB.addUser(user3);

        // Sample games
        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        gameDB.addGame(game1);
        gameDB.addGame(game2);
        gameDB.addGame(game3);

        // Sample auth tokens
        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        authDB.addAuth(token1);
        authDB.addAuth(token2);
        authDB.addAuth(token3);

        // Run the clear service
        ClearService service = new ClearService(authDB, gameDB, userDB);
        service.clearDB();

        // Validate everything was cleared
        assertEquals(0, userDB.listUsers().size(), "Users table should be empty after clearing");
        assertEquals(0, gameDB.listGames().size(), "Games table should be empty after clearing");
        assertEquals(0, authDB.listAuths().size(), "Auth table should be empty after clearing");
    }
}
