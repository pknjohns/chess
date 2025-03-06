package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    void doClearDB() throws DataAccessException {
        MemoryAuthDAO authDB = new MemoryAuthDAO();
        MemoryGameDAO gameDB = new MemoryGameDAO();
        MemoryUserDAO userDB = new MemoryUserDAO();

        var user1 = new UserData("pk", "1234", "pk@cs.com");
        var user2 = new UserData("kk", "5678", "kk@cs.com");
        var user3 = new UserData("pp", "1234", "cs.com");

        userDB.addUser(user1);
        userDB.addUser(user2);
        userDB.addUser(user3);

        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        gameDB.addGame(game1);
        gameDB.addGame(game2);
        gameDB.addGame(game3);

        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        authDB.addAuth(token1);
        authDB.addAuth(token2);
        authDB.addAuth(token3);

        ClearService service = new ClearService(authDB, gameDB, userDB);
        service.clearDB();
        assertEquals(0, userDB.listUsers().size());
        assertEquals(0, gameDB.listGames().size());
        assertEquals(0, authDB.listAuths().size());
    }
}
