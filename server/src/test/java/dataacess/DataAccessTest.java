package dataacess;

// import data access exception
import chess.ChessGame;
import dataaccess.*;

// import model classes
import model.*;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    //------------------------------------------------------
    // custom methods used to make tests work
    //------------------------------------------------------

    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws DataAccessException {
        DataAccess db = new MemoryDataAccess();

        db.deleteAllUsers();
        db.deleteAllGames();
        db.deleteAllTokens();
        return db;
    }

    public static void assertUserCollectionEqual(Collection<UserData> expected, Collection<UserData> actual) {
        assertEquals(expected.size(), actual.size(), "Expected and Actual are not the same length");
        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "Expected and Actual elements are not the same");
    }

    public static void assertGameCollectionEqual(Collection<GameData> expected, Collection<GameData> actual) {
        assertEquals(expected.size(), actual.size(), "Expected and Actual are not the same length");
        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "Expected and Actual elements are not the same");
    }

    //-----------------------------------------------------------
    // Tests for user-related DataAccess methods
    //-----------------------------------------------------------

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class})
    void addUser(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var user = new UserData("pk", "1234", "pk@cs.com");
        assertDoesNotThrow(() -> dataAccess.addUser(user));
    }

    @ParameterizedTest
    @ValueSource(classes= {MemoryDataAccess.class})
    void listUsers(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var user1 = new UserData("pk", "1234", "pk@cs.com");
        var user2 = new UserData("kk", "5678", "kk@cs.com");
        var user3 = new UserData("pp", "1234", "cs.com");

        List<UserData> expected = new ArrayList<>();
        expected.add(dataAccess.addUser(user1));
        expected.add(dataAccess.addUser(user2));
        expected.add(dataAccess.addUser(user3));

        var actual = dataAccess.listUsers();
        assertUserCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes= {MemoryDataAccess.class})
    void deleteAllUsers(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var user1 = new UserData("pk", "1234", "pk@cs.com");
        var user2 = new UserData("kk", "5678", "kk@cs.com");

        dataAccess.addUser(user1);
        dataAccess.addUser(user2);

        dataAccess.deleteAllUsers();

        var actual = dataAccess.listUsers();
        assertEquals(0, actual.size());
    }

    //-----------------------------------------------------------
    // Tests for game-related DataAccess methods
    //-----------------------------------------------------------

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class})
    void addGame(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game = new GameData(1234, "white", "black", "test", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.addGame(game));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class})
    void listGames(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        List<GameData> expected = new ArrayList<>();
        expected.add(dataAccess.addGame(game1));
        expected.add(dataAccess.addGame(game2));
        expected.add(dataAccess.addGame(game3));

        var actual = dataAccess.listGames();
        assertGameCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class})
    void deleteAllGames(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        dataAccess.addGame(game1);
        dataAccess.addGame(game2);
        dataAccess.addGame(game3);

        dataAccess.deleteAllGames();

        var actual = dataAccess.listGames();
        assertEquals(0, actual.size(), "Actual size is not 0 as expected");
    }

    //-----------------------------------------------------------
    // Tests for token-related DataAccess methods
    //-----------------------------------------------------------

    @ParameterizedTest
    @ValueSource(classes = {MemoryDataAccess.class})
    void addToken(Class<? extends DataAccess> dbClass) throws DataAccessException {
        DataAccess dataAccess = getDataAccess(dbClass);

        var token = new AuthData("1234","PK");
        assertDoesNotThrow(()-> dataAccess.addToken(token));
    }
}
