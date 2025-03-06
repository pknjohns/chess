package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.ListGameData;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private static AuthDAO authDB;
    private static GameDAO gameDB;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void init() {
        authDB = new MemoryAuthDAO();
        gameDB = new MemoryGameDAO();
        UserDAO userDB = new MemoryUserDAO();
        gameService = new GameService(authDB, gameDB);
        userService = new UserService(authDB, userDB);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDB.deleteAllAuths();
        gameDB.deleteAllGames();
    }

    @Test
    public void doListGames() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        gameDB.addGame(game1);
        gameDB.addGame(game2);
        gameDB.addGame(game3);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);

        Collection<ListGameData> gameList = gameService.listGames(result.authToken());
        assertEquals(gameDB.listGames().size(), gameList.size());
    }

    @Test
    public void noListGamesBadToken() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        gameDB.addGame(game1);
        gameDB.addGame(game2);
        gameDB.addGame(game3);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        assertThrows(UnauthorizedException.class, () -> gameService.listGames(""));
    }
}
