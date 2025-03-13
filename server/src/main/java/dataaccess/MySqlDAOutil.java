package dataaccess;

import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class MySqlDAOutil {

    public static void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            setParameters(ps, params);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + statement + " " + e.getMessage());
        }
    }

    public static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            switch (params[i]) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case AuthData p -> ps.setString(i + 1, p.toString());
                case null -> ps.setNull(i + 1, NULL);
                default -> {}
            }
        }
    }

    public static void configureAuthDatabase(String[] createStatements) throws DataAccessException {
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
