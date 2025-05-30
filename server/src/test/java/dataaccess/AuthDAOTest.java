package dataaccess;

// import model classes
import model.AuthData;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static dataaccess.DAOTestUtil.*;

class AuthDAOTest {

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

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class, MemoryAuthDAO.class})
    void addAuth(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        var token = new AuthData("1234","PK");
        assertDoesNotThrow(()-> dataAccess.addAuth(token));
        dataAccess.deleteAllAuths();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class, MemoryAuthDAO.class})
    void getAuth(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        String token = "1234";
        var auth = new AuthData("1234","PK");
        dataAccess.addAuth(auth);
        AuthData result = dataAccess.getAuth(token);
        assertEquals(auth, result);
        dataAccess.deleteAllAuths();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class, MemoryAuthDAO.class})
    void listAuths(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        List<AuthData> expected = new ArrayList<>();
        expected.add(dataAccess.addAuth(token1));
        expected.add(dataAccess.addAuth(token2));
        expected.add(dataAccess.addAuth(token3));

        var actual = dataAccess.listAuths();
        assertCollectionEqual(expected, actual);
        dataAccess.deleteAllAuths();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class, MemoryAuthDAO.class})
    void deleteAuth(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);
        AuthData auth = new AuthData("1234", "PK");

        dataAccess.addAuth(auth);

        dataAccess.deleteAuth("1234");
        assertEquals(0, dataAccess.listAuths().size());
        dataAccess.deleteAllAuths();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class, MemoryAuthDAO.class})
    void deleteAllAuths(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        dataAccess.addAuth(token1);
        dataAccess.addAuth(token2);
        dataAccess.addAuth(token3);

        dataAccess.deleteAllAuths();

        var actual = dataAccess.listAuths();
        assertEquals(0, actual.size(), "Actual size is not 0 as expected");
    }
}
