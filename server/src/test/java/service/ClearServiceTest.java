package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    void doClearDB() throws DataAccessException {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        var user1 = new UserData("pk", "1234", "pk@cs.com");
        var user2 = new UserData("kk", "5678", "kk@cs.com");
        var user3 = new UserData("pp", "1234", "cs.com");

        dataAccess.addUser(user1);
        dataAccess.addUser(user2);
        dataAccess.addUser(user3);

        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        dataAccess.addGame(game1);
        dataAccess.addGame(game2);
        dataAccess.addGame(game3);

        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        dataAccess.addToken(token1);
        dataAccess.addToken(token2);
        dataAccess.addToken(token3);

        ClearService service = new ClearService(dataAccess);
        service.clearDB();
        assertEquals(0, dataAccess.listUsers().size());
        assertEquals(0, dataAccess.listGames().size());
        assertEquals(0, dataAccess.listTokens().size());
    }
}
