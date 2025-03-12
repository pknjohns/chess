package dataacess;

import dataaccess.*;

// import model classes
import model.UserData;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

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

    public static void assertUserCollectionEqual(Collection<UserData> expected, Collection<UserData> actual) {
        assertEquals(expected.size(), actual.size(), "Expected and Actual are not the same length");
        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "Expected and Actual elements are not the same");
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class, MemoryUserDAO.class})
    void addUser(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        var user = new UserData("pk", "1234", "pk@cs.com");
        assertDoesNotThrow(() -> dataAccess.addUser(user));
    }

    @ParameterizedTest
    @ValueSource(classes= {MySqlUserDAO.class, MemoryUserDAO.class})
    void doGetUser(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        String username = "pk";
        var user = new UserData(username, "1234", "pk@cs.com");
        dataAccess.addUser(user);

        UserData result = dataAccess.getUser(username);
        assertEquals(result, user, "Successfully gets UserData");
    }

    @ParameterizedTest
    @ValueSource(classes= {MySqlUserDAO.class, MemoryUserDAO.class})
    void noGetUser(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        String username = "pk";
        UserData result = dataAccess.getUser(username);
        assertNull(result, "No user found");
    }

    @ParameterizedTest
    @ValueSource(classes= {MySqlUserDAO.class, MemoryUserDAO.class})
    void listUsers(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        String username1 = "pk";
        String username2 = "kk";
        String username3 = "pp";

        var user1 = new UserData(username1, "1234", "pk@cs.com");
        var user2 = new UserData(username2, "5678", "kk@cs.com");
        var user3 = new UserData(username3, "1234", "cs.com");

        List<UserData> expected = new ArrayList<>();
        expected.add(dataAccess.addUser(user1));
        expected.add(dataAccess.addUser(user2));
        expected.add(dataAccess.addUser(user3));

        var actual = dataAccess.listUsers();
        assertUserCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes= {MySqlUserDAO.class, MemoryUserDAO.class})
    void deleteAllUsers(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        var user1 = new UserData("pk", "1234", "pk@cs.com");
        var user2 = new UserData("kk", "5678", "kk@cs.com");

        dataAccess.addUser(user1);
        dataAccess.addUser(user2);

        dataAccess.deleteAllUsers();

        var actual = dataAccess.listUsers();
        assertEquals(0, actual.size());
    }


}
