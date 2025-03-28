package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import facade.BadRequestException;

import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    static Stream<Arguments> providedClasses() {
        return Stream.of(
                Arguments.of(MySqlAuthDAO.class, MySqlGameDAO.class, MySqlUserDAO.class),
                Arguments.of(MemoryAuthDAO.class, MemoryGameDAO.class, MemoryUserDAO.class)
        );
    }

    private GameServiceTestUtil initializeTest(Class<? extends AuthDAO> authDaoClassName,
                                               Class<? extends GameDAO> gameDaoClassName,
                                               Class<? extends UserDAO> userDaoClassName)
        throws DataAccessException {

        AuthDAO authDB = ServiceTestUtil.getAuthDataAccess(authDaoClassName);
        GameDAO gameDB = ServiceTestUtil.getGameDataAccess(gameDaoClassName);
        UserDAO userDB = ServiceTestUtil.getUserDataAccess(userDaoClassName);

        return new GameServiceTestUtil(authDB, gameDB, userDB);
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doListGames(Class<? extends AuthDAO> authDaoClassName,
                            Class<? extends GameDAO> gameDaoClassName,
                            Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        addData(setup.gameDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);

        Collection<ListGameData> gameList = setup.gameService.listGames(result.authToken());
        assertEquals(setup.gameDB.listGames().size(), gameList.size());
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noListGamesBadToken(Class<? extends AuthDAO> authDaoClassName,
                                    Class<? extends GameDAO> gameDaoClassName,
                                    Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        addData(setup.gameDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        setup.userService.registerUser(request);

        assertThrows(UnauthorizedException.class, () -> setup.gameService.listGames(""));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doCreateGame(Class<? extends AuthDAO> authDaoClassName,
                             Class<? extends GameDAO> gameDaoClassName,
                             Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);
        assertEquals(1, newGameID);
        assertEquals(1, setup.gameDB.listGames().size());
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noCreateGame(Class<? extends AuthDAO> authDaoClassName,
                             Class<? extends GameDAO> gameDaoClassName,
                             Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        setup.userService.registerUser(request);

        String gameName = "first";
        assertThrows(UnauthorizedException.class, () -> setup.gameService.createGame("", gameName));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doJoinGameWhite(Class<? extends AuthDAO> authDaoClassName,
                                Class<? extends GameDAO> gameDaoClassName,
                                Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);
        setup.gameService.joinGame(authToken, "WHITE",newGameID);
        assertEquals("kk", setup.gameDB.getGame(newGameID).whiteUsername());
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameWhiteAlreadyTaken(Class<? extends AuthDAO> authDaoClassName,
                                            Class<? extends GameDAO> gameDaoClassName,
                                            Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);
        setup.gameService.joinGame(authToken, "WHITE",newGameID);

        String username1 = "pp";
        String password1 = "1234";
        String email1 = ".com";

        RegisterRequest request1 = new RegisterRequest(username1, password1, email1);
        RegisterResult result1 = setup.userService.registerUser(request1);
        String authToken1 = result1.authToken();

        assertThrows(AlreadyTakenException.class, () -> setup.gameService.joinGame(authToken1, "WHITE", newGameID));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameWhiteBadToken(Class<? extends AuthDAO> authDaoClassName,
                                        Class<? extends GameDAO> gameDaoClassName,
                                        Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);

        assertThrows(UnauthorizedException.class, () -> setup.gameService.joinGame("", "WHITE",newGameID));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameBadColor(Class<? extends AuthDAO> authDaoClassName,
                                   Class<? extends GameDAO> gameDaoClassName,
                                   Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);

        assertThrows(BadRequestException.class, () -> setup.gameService.joinGame(authToken, "pink",newGameID));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void doJoinGameBlack(Class<? extends AuthDAO> authDaoClassName,
                                Class<? extends GameDAO> gameDaoClassName,
                                Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);
        setup.gameService.joinGame(authToken, "BLACK",newGameID);
        assertEquals("kk", setup.gameDB.getGame(newGameID).blackUsername());
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameBlackAlreadyTaken(Class<? extends AuthDAO> authDaoClassName,
                                            Class<? extends GameDAO> gameDaoClassName,
                                            Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);
        setup.gameService.joinGame(authToken, "BLACK",newGameID);

        String username1 = "pp";
        String password1 = "1234";
        String email1 = ".com";

        RegisterRequest request1 = new RegisterRequest(username1, password1, email1);
        RegisterResult result1 = setup.userService.registerUser(request1);
        String authToken1 = result1.authToken();

        assertThrows(AlreadyTakenException.class, () -> setup.gameService.joinGame(authToken1, "BLACK", newGameID));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    public void noJoinGameBlackBadToken(Class<? extends AuthDAO> authDaoClassName,
                                        Class<? extends GameDAO> gameDaoClassName,
                                        Class<? extends UserDAO> userDaoClassName)
            throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {

        GameServiceTestUtil setup = initializeTest(authDaoClassName, gameDaoClassName, userDaoClassName);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = setup.userService.registerUser(request);
        String authToken = result.authToken();

        String gameName = "first";
        int newGameID = setup.gameService.createGame(authToken, gameName);

        assertThrows(UnauthorizedException.class, () -> setup.gameService.joinGame("", "BLACK",newGameID));
    }

    private void addData(GameDAO gameDB) throws DataAccessException {
        var game1 = new GameData(1234, "white", "black", "test", new ChessGame());
        var game2 = new GameData(5678, "white", "black", "test", new ChessGame());
        var game3 = new GameData(2345, "white", "black", "test", new ChessGame());

        gameDB.addGame(game1);
        gameDB.addGame(game2);
        gameDB.addGame(game3);
    }
}
