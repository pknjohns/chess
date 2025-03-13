package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserServiceTest {

    private static AuthDAO authDB;
    private static UserDAO userDB;
    private static UserService userService;

    @BeforeAll
    public static void init() throws DataAccessException{
        authDB = new MySqlAuthDAO();
        userDB = new MySqlUserDAO();
        userService = new UserService(authDB, userDB);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDB.deleteAllAuths();
        userDB.deleteAllUsers();
    }

    @AfterAll
    public static void cleanup() throws DataAccessException {
        authDB.deleteAllAuths();
        userDB.deleteAllUsers();
    }

    @Test
    void doFindUser() throws DataAccessException {
        String username = "pk";

        UserData user = new UserData(username, "1234", "pk@cs.com");
        userDB.addUser(user);

        UserData result = userService.findUsername(username);
        assertEquals(user, result);
    }

    @Test
    void noFindUser() throws DataAccessException {
        UserData user1 = new UserData("pk", "1234", "pk@cs.com");
        userDB.addUser(user1);

        String username2 = "kk";
        UserData result = userService.findUsername(username2);
        assertNull(result);
    }

    @Test
    void doCreateUser() throws DataAccessException {
        UserData user1 = new UserData("pk", "1234", "pk@cs.com");
        userService.createUser(user1);
        int result = userDB.listUsers().size();
        assertEquals(1, result);
    }

    @Test
    void doCreateAuth() throws DataAccessException {
        String username = "pk";
        userService.createAuth(username);
        int result = authDB.listAuths().size();
        assertEquals(1, result);
    }

    @Test
    void doRegisterUser() throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);

        UserData expectedUser = new UserData(username, password, email);
        String expectedToken = result.authToken();
        AuthData expectedAuth = new AuthData(expectedToken, username);

        UserData actualUser = userDB.getUser(username);
        AuthData actualAuth = authDB.getAuth(expectedToken);

        assertEquals(expectedUser.username(), actualUser.username(), "usernames don't match");
        assertTrue(BCrypt.checkpw(password, actualUser.password()), "password don't match");
        assertEquals(expectedUser.email(), actualUser.email(), "emails don't match");
        assertEquals(expectedAuth, actualAuth);
    }

    @Test
    void noRegisterUserBadRequestNoEmail() {
        String username = "kk";
        String password = "1234";
        String email = null;

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @Test
    void noRegisterUserBadRequestNoUsername() {
        String username = null;
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @Test
    void noRegisterUserBadRequestNoPassword() {
        String username = "kk";
        String password = null;
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @Test
    void noRegisterUserAlreadyTaken() throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        assertThrows(AlreadyTakenException.class, () -> userService.registerUser(request));
    }

    @Test
    void doLoginUser() throws AlreadyTakenException, BadRequestException, UnauthorizedException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        LoginRequest lRequest = new LoginRequest(username, password);
        assertDoesNotThrow(() -> userService.loginUser(lRequest));
    }

    @Test
    void noLoginUserBadUsername() throws AlreadyTakenException, BadRequestException, UnauthorizedException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        LoginRequest lRequest = new LoginRequest("ll", "1234");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(lRequest));
    }

    @Test
    void noLoginUserBadPassword() throws AlreadyTakenException, BadRequestException, UnauthorizedException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        LoginRequest lRequest = new LoginRequest(username, "");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(lRequest));
    }

    @Test
    void doLogoutUser() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);

        String authToken = result.authToken();
        userService.logoutUser(authToken);
        assertEquals(0, authDB.listAuths().size());
    }

    @Test
    void noLogoutUser() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);
        String authToken = "4567";

        assertThrows(UnauthorizedException.class, () -> userService.logoutUser(authToken));
    }
}
