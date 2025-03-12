package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        configureUserDatabase();
    }

    public UserData addUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email, userData) VALUES (?, ?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email(), user);
        return user;
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, userData FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
        return null;
    }

    public Collection<UserData> listUsers() throws DataAccessException {
        Collection<UserData> users = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, userData FROM users";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        users.add(readUser(rs));
                    }
                }
            }
            return users;
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }

    public void deleteAllUsers() throws DataAccessException {
        String statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    //-------------------------------------------------------------------------------------------------------------
    // Helper Functions
    //-------------------------------------------------------------------------------------------------------------

    private UserData readUser(ResultSet rs) throws SQLException {
        String json = rs.getString("userData");
        return new Gson().fromJson(json, UserData.class);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case UserData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + statement + e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` VARCHAR(255) NOT NULL,
              `password` TEXT NOT NULL,
              `email` TEXT NOT NULL,
              `userData` TEXT NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureUserDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: " + e.getMessage());
        }
    }
}
