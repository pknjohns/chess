package dataaccess;

// import data access exception
import chess.ChessGame;

// import model classes
import model.GameData;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static dataaccess.DAOTestUtil.*;

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

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void addGame(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

        var game = new GameData(1234,"white", "black", "test", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.addGame(game));
        dataAccess.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void doGetGame(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

        GameData game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        int newID = dataAccess.addGame(game1);

        GameData actual = dataAccess.getGame(newID);
        assertEquals(game1.gameName(), actual.gameName(), "Successfully gets GameData");
        assertEquals(game1.whiteUsername(), actual.whiteUsername());
        assertEquals(game1.blackUsername(), actual.blackUsername());
        dataAccess.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void noGetGame(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

        GameData game1 = new GameData(1234, "white", "black", "test", null);
        dataAccess.addGame(game1);

        GameData actual = dataAccess.getGame(2345);
        assertNull(actual, "No game found");
        dataAccess.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void listGames(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);

        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        List<GameData> expected = new ArrayList<>();
        expected.add(game1);
        expected.add(game2);
        expected.add(game3);

        dataAccess.addGame(game1);
        dataAccess.addGame(game2);
        dataAccess.addGame(game3);

        var actual = dataAccess.listGames();
        assertCollectionEqual(expected, actual);
        dataAccess.deleteAllGames();
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
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void doUpdateGameWhitePlayer(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);
        var game1 = new GameData(1234, null, "black", "test", new ChessGame());
        int newID = dataAccess.addGame(game1);
        int expectedLen = dataAccess.listGames().size();

        dataAccess.updateGameWhitePlayer(newID, "white");

        int actualLen = dataAccess.listGames().size();
        assertEquals(expectedLen, actualLen);

        GameData actualGame = dataAccess.getGame(newID);
        String actualWhiteName = actualGame.whiteUsername();
        assertEquals("white",actualWhiteName, "whiteUsername did not update");
        dataAccess.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class, MemoryGameDAO.class})
    void doUpdateGameBlackPlayer(Class<? extends GameDAO> dbClass) throws DataAccessException {
        GameDAO dataAccess = getGameDataAccess(dbClass);
        var game1 = new GameData(1234, "white", null, "test", new ChessGame());
        int newID = dataAccess.addGame(game1);

        int expectedLen = dataAccess.listGames().size();

        dataAccess.updateGameBlackPlayer(newID, "black");
        int actualLen = dataAccess.listGames().size();

        assertEquals(expectedLen, actualLen);
        assertEquals("black",dataAccess.getGame(newID).blackUsername());
        dataAccess.deleteAllGames();
    }

}
