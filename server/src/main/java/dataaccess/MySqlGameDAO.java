package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.Collection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.MySqlDAOutil.*;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(255),
              `blackUsername` VARCHAR(255),
              `gameName` VARCHAR(255) NOT NULL,
              `game` TEXT,
              PRIMARY KEY (`gameID`),
              INDEX(gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        configureDatabase(createStatements);
    }

    public Integer addGame(GameData game) throws DataAccessException {
        String statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        return executeUpdate(statement, game.gameName(), new Gson().toJson(new ChessGame()));
    }

    public void updateGameWhitePlayer(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET whiteUsername=? WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }

    public void updateGameBlackPlayer(int gameID, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET blackUsername=? WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return makeGameData(gameID, rs);
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
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(makeGameData(Integer.parseInt(rs.getString("gameID")), rs));
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

    private GameData makeGameData(int gameID, ResultSet rs) throws SQLException {
        String white = rs.getString("whiteUsername");
        String black = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, white, black, gameName, game);
    }
}
