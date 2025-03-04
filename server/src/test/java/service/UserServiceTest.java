package service;

import dataaccess.*;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private static AuthDAO authDB;
    private static UserDAO userDB;
    private static UserService userService;

    @BeforeAll
    public static void init() {
        authDB = new MemoryAuthDAO();
        userDB = new MemoryUserDAO();
        userService = new UserService(authDB, userDB);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDB.deleteAllTokens();
        userDB.deleteAllUsers();
    }

    @Test
    void doFindUser() throws DataAccessException {
        UserData user = new UserData("pk", "1234", "pk@cs.com");
        userDB.addUser(user);

        UserData result = userService.findUsername(user);
        assertEquals(user, result);
    }

    @Test
    void noFindUser() throws DataAccessException {
        UserData user1 = new UserData("pk", "1234", "pk@cs.com");
        userDB.addUser(user1);

        UserData user2 = new UserData("kk", "5678", "kk@cs.com");
        UserData result = userService.findUsername(user2);
        assertNull(result);
    }
}
