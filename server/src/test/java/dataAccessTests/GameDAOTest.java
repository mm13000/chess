package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;


class GameDAOTest {
    private final GameDAO gameDAO = new GameDAOmySQL();
    private final AuthDAO authDAO = new AuthDAOmySQL();
    private final UserDAO userDAO = new UserDAOmySQL();

    @BeforeEach
    void setUp() {
        // Clear the database
        try {
            gameDAO.clearGames();
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (DataAccessException e) {
            Assertions.fail("DataAccess exception thrown when clearing database: " + e.getMessage());
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown when clearing database: " + e.getMessage());
        }
    }

    @Test
    void clearGames() {
        try {
            gameDAO.clearGames();
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games");
            assertFalse(stmt.executeQuery().next());
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void createGameSuccess() {
        int gameID = -1;
        try {
            gameID = gameDAO.createGame("testgame");
        } catch (DataAccessException e) {
            Assertions.fail("Game creation unsuccessful. Exception thrown: " + e.getMessage());
        }
        Assertions.assertTrue(gameID >= 0, "Game creation unsuccessful. Game ID not >= 0.");
    }

    @Test
    void listGames() {
        // First try listing games when none have been added and check that the returned list is empty
        try {
            Assertions.assertTrue(gameDAO.listGames().isEmpty(), "Game listing unsuccessful. List not empty when no games have been added.");
        } catch (DataAccessException e) {
            Assertions.fail("Game listing unsuccessful. Exception thrown: " + e.getMessage());
        }

        // Create two games
        String gameName1 = "testgame1";
        String gameName2 = "testgame2";
        try {
            gameDAO.createGame(gameName1);
            gameDAO.createGame(gameName2);
        } catch (DataAccessException e) {
            Assertions.fail("Game creation unsuccessful. Exception thrown: " + e.getMessage());
        }
        // Try to get a list of games
        HashSet<GameData> returnedGames = null;
        try {
            returnedGames = new HashSet<>(gameDAO.listGames());
            Assertions.assertNotNull(returnedGames, "Game listing unsuccessful. Null returned.");
        } catch (DataAccessException e) {
            Assertions.fail("Game listing unsuccessful. Exception thrown: " + e.getMessage());
        }
        // Check that the games are in the list
        ArrayList<String> gameNames = new ArrayList<>();
        for (GameData game : returnedGames) {
            gameNames.add(game.gameName());
        }
        Assertions.assertTrue(gameNames.contains(gameName1), "Game listing unsuccessful. Game 1 not found.");
        Assertions.assertTrue(gameNames.contains(gameName2), "Game listing unsuccessful. Game 2 not found.");
    }

    @Test
    void getGame() {
        // Create a couple of games
        String gameName1 = "testgame1";
        String gameName2 = "testgame2";
        int gameID1 = -1;
        int gameID2 = -1;
        try {
            gameID1 = gameDAO.createGame(gameName1);
            Assertions.assertTrue(gameID1 >= 0, "Game creation unsuccessful. Game ID not >= 0.");
            gameID2 = gameDAO.createGame(gameName2);
            Assertions.assertTrue(gameID2 >= 0, "Game creation unsuccessful. Game ID not >= 0.");
        } catch (DataAccessException e) {
            Assertions.fail("Game creation unsuccessful. Exception thrown: " + e.getMessage());
        }
        // Try to get the games
        GameData returnedGame1;
        GameData returnedGame2;
        try {
            returnedGame1 = gameDAO.getGame(gameID1);
            returnedGame2 = gameDAO.getGame(gameID2);
            Assertions.assertNotNull(returnedGame1, "Game retrieval unsuccessful. Null returned for game 1.");
            Assertions.assertNotNull(returnedGame2, "Game retrieval unsuccessful. Null returned for game 2.");
        } catch (DataAccessException e) {
            Assertions.fail("Game retrieval unsuccessful. Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void updateGame() {
        // Add a user so that a valid username exists in the database
        UserData testUser = new UserData("testUser", "password", "testuser@me.com");
        try {
            userDAO.addUser(testUser);
        } catch (DataAccessException e) {
            Assertions.fail("User addition unsuccessful. Exception thrown: " + e.getMessage());
        }

        // Create a couple of games
        String gameName1 = "testgame1";
        String gameName2 = "testgame2";
        int gameID1 = -1;
        int gameID2 = -1;
        try {
            gameID1 = gameDAO.createGame(gameName1);
            Assertions.assertTrue(gameID1 >= 0, "Game creation unsuccessful. Game ID not >= 0.");
            gameID2 = gameDAO.createGame(gameName2);
            Assertions.assertTrue(gameID2 >= 0, "Game creation unsuccessful. Game ID not >= 0.");
        } catch (DataAccessException e) {
            Assertions.fail("Game creation unsuccessful. Exception thrown: " + e.getMessage());
        }

        // variables to hold the returned games
        GameData game1;
        GameData game2;

        // Add a user to the first game
        try {
            game1 = gameDAO.getGame(gameID1);
            game1 = new GameData(game1.gameID(), testUser.username(), game1.blackUsername(), game1.gameName(), game1.game());
            gameDAO.updateGame(gameID1, game1);
        } catch (DataAccessException e) {
            Assertions.fail("Game modification unsuccessful. Exception thrown: " + e.getMessage());
        }
        // Check that the user was added
        try {
            game1 = gameDAO.getGame(gameID1);
            Assertions.assertEquals(testUser.username(), game1.whiteUsername(), "Game modification unsuccessful. User not added to game 1.");
        } catch (DataAccessException e) {
            Assertions.fail("Game modification unsuccessful. Exception thrown: " + e.getMessage());
        }

        // Add a user to the second game
        try {
            game2 = gameDAO.getGame(gameID2);
            game2 = new GameData(game2.gameID(), game2.whiteUsername(), testUser.username(), game2.gameName(), game2.game());
            gameDAO.updateGame(gameID2, game2);
        } catch (DataAccessException e) {
            Assertions.fail("Game modification unsuccessful. Exception thrown: " + e.getMessage());
        }
        // Check that the user was added
        try {
            game2 = gameDAO.getGame(gameID2);
            Assertions.assertEquals(testUser.username(), game2.blackUsername(), "Game modification unsuccessful. User not added to game 2.");
        } catch (DataAccessException e) {
            Assertions.fail("Game modification unsuccessful. Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void createGameNullName() {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGameBadID() {
        // Try when no games have been added
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(-1));

        // Add some games and try again
        try {
            gameDAO.createGame("testgame1");
            gameDAO.createGame("testgame2");
        } catch (DataAccessException e) {
            Assertions.fail("Game creation unsuccessful. Exception thrown: " + e.getMessage());
        }
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(-1));
    }

    @Test
    void updateGameBadID() {
        // First try when no games have been added
        GameData gameData = new GameData(-1, "white", "black", "game", new ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(-1, gameData));

        // Add a user so that a valid username exists in the database
        UserData testUser = new UserData("testUser", "password", "testUserEmail");
        try {
            userDAO.addUser(testUser);
        } catch (DataAccessException e) {
            Assertions.fail("User addition unsuccessful. Exception thrown: " + e.getMessage());
        }

        // Add a game and try accessing again
        try {
            gameDAO.createGame("testgame1");
        } catch (DataAccessException e) {
            Assertions.fail("Game creation unsuccessful. Exception thrown: " + e.getMessage());
        }
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(-1, gameData));
    }
}