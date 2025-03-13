package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.ListGameData;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

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

    static Stream<Arguments> providedClasses() {
        return Stream.of(
                Arguments.of(MySqlAuthDAO.class, MySqlGameDAO.class, MySqlUserDAO.class),
                Arguments.of(MemoryAuthDAO.class, MemoryGameDAO.class, MemoryUserDAO.class)
        );
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doListGames(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noListGamesBadToken(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doCreateGame(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noCreateGame(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        String gameName = "first";
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("", gameName));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doJoinGameWhite(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameWhiteAlreadyTaken(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameWhiteBadToken(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameBadColor(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doJoinGameBlack(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameBlackAlreadyTaken(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameBlackBadToken(Class<? extends AuthDAO> authDaoClassName, Class<? extends GameDAO> gameDaoClassName, Class<? extends UserDAO> userDaoClassName) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = getGameDataAccess(gameDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        GameService gameService = new GameService(authDB, gameDB);
        UserService userService = new UserService(authDB, userDB);

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
