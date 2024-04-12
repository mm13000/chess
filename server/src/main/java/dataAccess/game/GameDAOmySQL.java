package dataAccess.game;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDAOmySQL implements GameDAO {
    @Override
    public void clearGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("DELETE FROM games").executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        String gameString = new Gson().toJson(game, ChessGame.class);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO games (gameName, game_data) values (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, gameName);
                stmt.setString(2, gameString);
                if (stmt.executeUpdate() > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else throw new DataAccessException("No generated gameID found");
                } else throw new DataAccessException("Unable to add game to database");
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT gameID, whiteUsername, blackUsername, gameName FROM games";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                ArrayList<GameData> gameList = new ArrayList<>();
                while (rs.next()) {
                    // Unpack the ResultSet with the game information
                    int gameID = rs.getInt("gameID");
                    String whiteUsername = rs.getNString("whiteUsername");
                    String blackUsername = rs.getNString("blackUsername");
                    String gameName = rs.getNString("gameName");
                    // Make a new GameData object and add it to the gameList
                    GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, null);
                    gameList.add(game);
                }
                return gameList;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = """
                    SELECT gameID, whiteUsername, blackUsername, gameName, game_data
                    FROM games
                    WHERE gameID=?
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Unpack the ResultSet with the game information
                    int id = rs.getInt("gameID");
                    String whiteUsername = rs.getNString("whiteUsername");
                    String blackUsername = rs.getNString("blackUsername");
                    String gameName = rs.getNString("gameName");
                    String gameData = rs.getNString("game_data");
                    ChessGame chessGame = new Gson().fromJson(gameData, ChessGame.class);
                    // return the game info
                    return new GameData(id, whiteUsername, blackUsername, gameName, chessGame);
                } else throw new DataAccessException("No game with gameID found");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        Gson gson = new GsonBuilder().serializeNulls().create();
        String gameString = gson.toJson(updatedGame.game());
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = """
                    UPDATE games
                    SET whiteUsername=?, blackUsername=?, gameName=?, game_data=?
                    WHERE gameID=?
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, updatedGame.whiteUsername());
                stmt.setString(2, updatedGame.blackUsername());
                stmt.setString(3, updatedGame.gameName());
                stmt.setString(4, gameString);
                stmt.setInt(5, gameID);
                if (stmt.executeUpdate() == 0) {
                    throw new DataAccessException("Failed to update game");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
