package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.util.Collection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Types.NULL;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        configureGameDatabase();
    }

    public GameData addGame(GameData game) throws DataAccessException {
        String statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game, gameData) VALUES (?, ?, ?, ?, ?, ?)";
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new Gson().toJson(game.game()), game);
        return game;
    }

    public void updateGameWhitePlayer(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET whiteUsername=?, gameData=? WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                // update gameData
                GameData oldGame = getGame(gameID);
                GameData newGame = new GameData(gameID, username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());

                ps.setString(1, username);
                ps.setString(2, newGame.toString());
                ps.setInt(3, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }

    public void updateGameBlackPlayer(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET blackUsername=?, gameData=? WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                // update gameData
                GameData oldGame = getGame(gameID);
                GameData newGame = new GameData(gameID, oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());

                ps.setString(1, username);
                ps.setString(2, newGame.toString());
                ps.setInt(3, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameData FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, gameData FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
            return games;
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }

    public void deleteAllGames() throws DataAccessException {
        String statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    //-------------------------------------------------------------------------------------------------------------
    // Helper Functions
    //-------------------------------------------------------------------------------------------------------------

    private GameData readGame(ResultSet rs) throws SQLException {
        String json = rs.getString("gameData");
        return new Gson().fromJson(json, GameData.class);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        //case ChessGame p -> ps.setString(i + 1, p.toString());
                        case GameData p -> ps.setString(i + 1, p.toString());
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
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(255),
              `blackUsername` VARCHAR(255),
              `gameName` VARCHAR(255) NOT NULL,
              `game` TEXT,
              `gameData` TEXT,
              PRIMARY KEY (`gameID`),
              INDEX(gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureGameDatabase() throws DataAccessException {
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
