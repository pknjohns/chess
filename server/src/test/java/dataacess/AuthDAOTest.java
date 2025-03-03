package dataacess;

// import data access exception
import dataaccess.*;

// import model classes
import model.AuthData;

// import test attributes
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {

    private AuthDAO getAuthDataAccess(Class<? extends AuthDAO> databaseClass) throws DataAccessException {
        AuthDAO db;

        if (databaseClass.equals(MemoryAuthDAO.class)) {
            db = new MemoryAuthDAO();
        } else {
            db = new MemoryAuthDAO(); // placeholder for when we add the MySqlDatabase
        }

        db.deleteAllTokens();
        return db;
    }

    public static void assertTokenCollectionEqual(Collection<AuthData> expected, Collection<AuthData> actual) {
        assertEquals(expected.size(), actual.size(), "Expected and Actual are not the same length");
        assertEquals(new HashSet<>(expected), new HashSet<>(actual), "Expected and Actual elements are not the same");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    void addToken(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        var token = new AuthData("1234","PK");
        assertDoesNotThrow(()-> dataAccess.addToken(token));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    void listTokens(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        List<AuthData> expected = new ArrayList<>();
        expected.add(dataAccess.addToken(token1));
        expected.add(dataAccess.addToken(token2));
        expected.add(dataAccess.addToken(token3));

        var actual = dataAccess.listTokens();
        assertTokenCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryAuthDAO.class})
    void deleteAllTokens(Class<? extends AuthDAO> dbClass) throws DataAccessException {
        AuthDAO dataAccess = getAuthDataAccess(dbClass);

        var token1 = new AuthData("1234", "PK");
        var token2 = new AuthData("2345", "PP");
        var token3 = new AuthData("3456", "KK");

        dataAccess.addToken(token1);
        dataAccess.addToken(token2);
        dataAccess.addToken(token3);

        dataAccess.deleteAllTokens();

        var actual = dataAccess.listTokens();
        assertEquals(0, actual.size(), "Actual size is not 0 as expected");
    }
}
