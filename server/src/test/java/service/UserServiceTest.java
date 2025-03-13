package service;

import dataaccess.*;

import model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mindrot.jbcrypt.BCrypt;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

//    private static AuthDAO authDB;
//    private static UserDAO userDB;
//    private static UserService userService;
//
//    @BeforeAll
//    public static void init() {
//        authDB = new MemoryAuthDAO();
//        userDB = new MemoryUserDAO();
//        userService = new UserService(authDB, userDB);
//    }
//
//    @BeforeEach
//    public void setup() throws DataAccessException {
//        authDB.deleteAllAuths();
//        userDB.deleteAllUsers();
//    }

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
                Arguments.of(MySqlAuthDAO.class, MySqlUserDAO.class),
                Arguments.of(MemoryAuthDAO.class, MemoryUserDAO.class)
        );
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void doFindUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "pk";

        UserData user = new UserData(username, "1234", "pk@cs.com");
        userDB.addUser(user);

        UserData result = userService.findUsername(username);
        assertEquals(user, result);
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noFindUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        UserData user1 = new UserData("pk", "1234", "pk@cs.com");
        userDB.addUser(user1);

        String username2 = "kk";
        UserData result = userService.findUsername(username2);
        assertNull(result);
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void doCreateUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        UserData user1 = new UserData("pk", "1234", "pk@cs.com");
        userService.createUser(user1);
        int result = userDB.listUsers().size();
        assertEquals(1, result);
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void doCreateAuth(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "pk";
        userService.createAuth(username);
        int result = authDB.listAuths().size();
        assertEquals(1, result);
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void doRegisterUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

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

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noRegisterUserBadRequestNoEmail(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = null;

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noRegisterUserBadRequestNoUsername(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = null;
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noRegisterUserBadRequestNoPassword(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = null;
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noRegisterUserAlreadyTaken(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        assertThrows(AlreadyTakenException.class, () -> userService.registerUser(request));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void doLoginUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        LoginRequest lRequest = new LoginRequest(username, password);
        assertDoesNotThrow(() -> userService.loginUser(lRequest));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noLoginUserBadUsername(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        LoginRequest lRequest = new LoginRequest("ll", "1234");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(lRequest));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noLoginUserBadPassword(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);

        LoginRequest lRequest = new LoginRequest(username, "");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(lRequest));
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void doLogoutUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = userService.registerUser(request);

        String authToken = result.authToken();
        userService.logoutUser(authToken);
        assertEquals(0, authDB.listAuths().size());
    }

    @ParameterizedTest
    @MethodSource("providedClasses")
    void noLogoutUser(Class<? extends AuthDAO> authDaoClassName, Class<? extends UserDAO> userDaoClassName) throws AlreadyTakenException, BadRequestException, DataAccessException {

        AuthDAO authDB = getAuthDataAccess(authDaoClassName);
        UserDAO userDB = getUserDataAccess(userDaoClassName);

        UserService userService = new UserService(authDB, userDB);

        String username = "kk";
        String password = "1234";
        String email = ".com";

        RegisterRequest request = new RegisterRequest(username, password, email);
        userService.registerUser(request);
        String authToken = "4567";

        assertThrows(UnauthorizedException.class, () -> userService.logoutUser(authToken));
    }
}
