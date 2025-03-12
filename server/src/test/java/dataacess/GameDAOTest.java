package dataacess;

// import data access exception
import chess.ChessGame;
import dataaccess.*;

// import model classes
import model.GameData;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
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

    public static void assertGameCollectionEqual(Collection<GameData> expected, Collection<GameData> actual) {
        assertEquals(expected.size(), actual.size(), "Expected and Actual are not the same length");
        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "Expected and Actual elements are not the same");
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void addGame(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

        var game = new GameData(1234, "white", "black", "test", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.addGame(game));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class})
    void listGames(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

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
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void deleteAllGames(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

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

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class})
    void doUpdateGameWhitePlayer(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);
        var game1 = new GameData(1234, null, "black", "test", new ChessGame());
        dataAccess.addGame(game1);
        int expectedLen = dataAccess.listGames().size();
        dataAccess.updateGameWhitePlayer(1234, "white");
        int actualLen = dataAccess.listGames().size();
        assertEquals(expectedLen, actualLen);
        assertEquals("white",dataAccess.getGame(1234).whiteUsername());
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class})
    void doUpdateGameBlackPlayer(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);
        var game1 = new GameData(1234, "white", null, "test", new ChessGame());
        dataAccess.addGame(game1);
        int expectedLen = dataAccess.listGames().size();
        dataAccess.updateGameBlackPlayer(1234, "black");
        int actualLen = dataAccess.listGames().size();
        assertEquals(expectedLen, actualLen);
        assertEquals("black",dataAccess.getGame(1234).blackUsername());
    }

}
