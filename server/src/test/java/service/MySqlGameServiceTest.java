package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.ListGameData;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MySqlGameServiceTest {

    private static AuthDAO authDB;
    private static GameDAO gameDB;
    private static UserDAO userDB;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void init() throws DataAccessException {
        authDB = new MySqlAuthDAO();
        gameDB = new MySqlGameDAO();
        userDB = new MySqlUserDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDB.deleteAllAuths();
        gameDB.deleteAllGames();
        userDB.deleteAllUsers();
        gameService = new GameService(authDB, gameDB);
        userService = new UserService(authDB, userDB);
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        authDB.deleteAllAuths();
        gameDB.deleteAllGames();
        userDB.deleteAllUsers();
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

    @Test
    public void doCreateGame() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);
        assertEquals(1, newGameID);
        assertEquals(1, gameDB.listGames().size());
    }

    @Test
    public void noCreateGame() throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        String gameName = "first";
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("", gameName));
    }

    @Test
    public void doJoinGameWhite() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);
        gameService.joinGame(authToken, "WHITE",newGameID);
        assertEquals("kk", gameDB.getGame(newGameID).whiteUsername());
    }

    @Test
    public void noJoinGameWhiteAlreadyTaken() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);
        gameService.joinGame(authToken, "WHITE",newGameID);

        String username1 = "pp";
        String password1 = "1234";
        String email1 = ".com";

        RegisterRequest request1 = new RegisterRequest(username1, password1, email1);
        RegisterResult result1 = userService.registerUser(request1);
        String authToken1 = result1.authToken();

        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(authToken1, "WHITE", newGameID));
    }

    @Test
    public void noJoinGameWhiteBadToken() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);

        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("", "WHITE",newGameID));
    }

    @Test
    public void noJoinGameBadColor() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);

        assertThrows(BadRequestException.class, () -> gameService.joinGame(authToken, "pink",newGameID));
    }

    @Test
    public void doJoinGameBlack() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);
        gameService.joinGame(authToken, "BLACK",newGameID);
        assertEquals("kk", gameDB.getGame(newGameID).blackUsername());
    }

    @Test
    public void noJoinGameBlackAlreadyTaken() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);
        gameService.joinGame(authToken, "BLACK",newGameID);

        String username1 = "pp";
        String password1 = "1234";
        String email1 = ".com";

        RegisterRequest request1 = new RegisterRequest(username1, password1, email1);
        RegisterResult result1 = userService.registerUser(request1);
        String authToken1 = result1.authToken();

        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(authToken1, "BLACK", newGameID));
    }

    @Test
    public void noJoinGameBlackBadToken() throws BadRequestException, AlreadyTakenException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = gameService.createGame(authToken, gameName);

        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("", "BLACK",newGameID));
    }
}
