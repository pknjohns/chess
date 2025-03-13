package dataaccess;

// import model classes
import model.UserData;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static dataaccess.DAOTestUtil.*;

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

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class, MemoryUserDAO.class})
    void addUser(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        var user = new UserData("pk", "1234", "pk@cs.com");
        assertDoesNotThrow(() -> dataAccess.addUser(user));
        dataAccess.deleteAllUsers();
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
        dataAccess.deleteAllUsers();
    }

    @ParameterizedTest
    @ValueSource(classes= {MySqlUserDAO.class, MemoryUserDAO.class})
    void noGetUser(Class<? extends UserDAO> daoClass) throws DataAccessException {
        UserDAO dataAccess = getUserDataAccess(daoClass);

        String username = "pk";
        UserData result = dataAccess.getUser(username);
        assertNull(result, "No user found");
        dataAccess.deleteAllUsers();
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
        assertCollectionEqual(expected, actual);
        dataAccess.deleteAllUsers();
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
