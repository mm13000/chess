package serviceTests;

import chess.ChessGame;
import dataAccess.auth.AuthDAO;
import dataAccess.game.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.GameHeader;
import result.ListGamesResult;
import service.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameServiceTests {
    ServiceTestFactory testFactory;
    GameService gameService;
    GameDAO gameDAO;
    AuthDAO authDAO;

    @BeforeEach
    public void Setup() {
        testFactory = new ServiceTestFactory();
        gameDAO = testFactory.gameDAO;
        authDAO = testFactory.authDAO;
        gameService = testFactory.gameService;
    }

    /*
     * Tests of GameService.createGame()
     */
    @Test
    public void createValidGame() {
        // Register a user and log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);

        // Then attempt to create a valid game
        CreateGameRequest request = new CreateGameRequest(auth.authToken(), "New Game!!");
        CreateGameResult result = null;
        try {
            result = gameService.createGame(request);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }

        // Then test that the result returned a valid gameID
        Assertions.assertTrue(result.gameID() > 0, "Returned invalid gameID");
    }

    @Test
    public void createMultipleGames() {
        // Register a user and log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);

        // Then attempt to create a couple valid game
        CreateGameRequest request1 = new CreateGameRequest(auth.authToken(), "New Game!!");
        CreateGameRequest request2 = new CreateGameRequest(auth.authToken(), "Another Game!");
        CreateGameResult result1 = null;
        CreateGameResult result2 = null;
        try {
            result1 = gameService.createGame(request1);
            result2 = gameService.createGame(request2);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }

        // Then test that each result returned a valid and unique gameID
        Assertions.assertTrue(result1.gameID() > 0, "Request 1 returned invalid gameID");
        Assertions.assertTrue(result2.gameID() > 0, "Request 2 returned invalid gameID");
        Assertions.assertNotEquals(result1.gameID(), result2.gameID(), "gameIDs were not unique");
    }

    @Test
    public void createGameBadRequest() {
        // Register a user and log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);

        // Create several bad requests
        CreateGameRequest badRequest1 = new CreateGameRequest(auth.authToken(), null);
        CreateGameRequest badRequest2 = new CreateGameRequest(auth.authToken(), "");
        CreateGameRequest badRequest3 = new CreateGameRequest(null, "New game");
        CreateGameRequest badRequest4 = new CreateGameRequest("", "New game");

        // Assert that in each case a ResponseException is thrown
        try {
            gameService.createGame(badRequest1);
            Assertions.fail("ResponseException not thrown with null gameName");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.BAD_REQUEST, e.statusCode());
        }
        try {
            gameService.createGame(badRequest2);
            Assertions.fail("ResponseException not thrown with empty gameName");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.BAD_REQUEST, e.statusCode());
        }
        try {
            gameService.createGame(badRequest3);
            Assertions.fail("ResponseException not thrown with null authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.BAD_REQUEST, e.statusCode());
        }
        try {
            gameService.createGame(badRequest4);
            Assertions.fail("ResponseException not thrown with empty authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.BAD_REQUEST, e.statusCode());
        }
    }

    @Test
    public void createGameUnauthorized() {
        // Attempt to create a game with no valid users in database
        CreateGameRequest request = new CreateGameRequest("fakeAuth", "Try Me!");
        try {
            gameService.createGame(request);
            Assertions.fail("ResponseException not thrown with no registered users");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.UNAUTHORIZED, e.statusCode());
        }

        // Register a user and log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        testFactory.loginUser(user);

        // Attempt to create a game using fake authToken
        try {
            gameService.createGame(request);
            Assertions.fail("ResponseException not thrown with invalid Auth Token");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.UNAUTHORIZED, e.statusCode());
        }
    }

    /*
     * Tests of GameService.ListGames
     */

    @Test
    public void listGamesValid() {
        // Register a user, log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);

        // Create three games
        ArrayList<String> gameNames = new ArrayList<>() {{add("game1"); add("crazy game"); add("u win");}};
        int gameID1 = testFactory.createGame(auth.authToken(), gameNames.get(0));
        int gameID2 = testFactory.createGame(auth.authToken(), gameNames.get(1));
        int gameID3 = testFactory.createGame(auth.authToken(), gameNames.get(2));

        // Attempt to get a list of the games
        ListGamesRequest request = new ListGamesRequest(auth.authToken());
        ListGamesResult result = null;
        try {
            result = gameService.listGames(request);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }

        // The actual returned list of games:
        HashMap<Integer, String> gamesActual = new HashMap<>();
        for (var game : result.games()) {
            gamesActual.put(game.gameID(), game.gameName());
        }

        // The expected list of games:
        HashMap<Integer, String> gamesExpected = new HashMap<>();
        gamesExpected.put(gameID1, gameNames.get(0));
        gamesExpected.put(gameID2, gameNames.get(1));
        gamesExpected.put(gameID3, gameNames.get(2));

        // Compare the returned sets of games:
        Assertions.assertEquals(gamesActual, gamesExpected);
    }

    @Test
    public void listGamesUnauthorized() {
        // Register a user, log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);

        // Create three games
        ArrayList<String> gameNames = new ArrayList<>() {{add("game1"); add("crazy game"); add("u win");}};
        testFactory.createGame(auth.authToken(), gameNames.get(0));
        testFactory.createGame(auth.authToken(), gameNames.get(1));
        testFactory.createGame(auth.authToken(), gameNames.get(2));

        // Attempt to list games using a fake auth token
        ListGamesRequest request = new ListGamesRequest("invalidAuth");
        try {
            gameService.listGames(request);
            Assertions.fail("Did not throw Exception when given invalid authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.UNAUTHORIZED, e.statusCode());
        }
    }

    /*
     * Tests of GameService.JoinGame
     */

    @Test
    public void joinGameValidPlayer() {
        // Register a user, log them in, and create a game
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);
        int gameID = testFactory.createGame(auth.authToken(), "New game");

        // Attempt to join game as White player
        JoinGameRequest request = new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.WHITE, gameID);
        try {
            gameService.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }
        // Attempt to join game as Black player (player playing themselves)
        request = new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.BLACK, gameID);
        try {
            gameService.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }

        // Check that the game has been updated in the database
        ListGamesResult listGamesResult = null;
        try {
            listGamesResult = gameService.listGames(new ListGamesRequest(auth.authToken()));
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }
        ArrayList<GameHeader> gameList = new ArrayList<>(listGamesResult.games());
        GameHeader game = gameList.getFirst();
        Assertions.assertEquals(game.whiteUsername(), user.username(), "White player was not set correctly");
        Assertions.assertEquals(game.blackUsername(), user.username(), "Black player was not set correctly");
    }

    @Test
    public void joinGameValidObserver() {
        // Register a user, log them in, and create a game
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);
        int gameID = testFactory.createGame(auth.authToken(), "New game");

        // Attempt to join game as observer
        JoinGameRequest request = new JoinGameRequest(auth.authToken(), null, gameID);
        try {
            gameService.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }
    }

    @Test
    public void joinGameInvalidGame() {
        // Register a user, log them in, and create a game
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);
        int gameID = testFactory.createGame(auth.authToken(), "New game");

        // Attempt to join a game that does not exist
        JoinGameRequest request = new JoinGameRequest(auth.authToken(), null, gameID + 1);
        try {
            gameService.joinGame(request);
            Assertions.fail("No exception thrown with invalid gameID");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.BAD_REQUEST, e.statusCode());
        }
    }

    @Test
    public void joinGamePlayerTaken() {
        // Register two users, log them in, and create a game
        UserData user1 = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        UserData user2 = testFactory.registerUser(new UserData("molly", "pass", "molly@me.com"));
        AuthData auth1 = testFactory.loginUser(user1);
        AuthData auth2 = testFactory.loginUser(user2);
        int gameID = testFactory.createGame(auth1.authToken(), "New game");

        // Have the first user join as the White player
        JoinGameRequest request = new JoinGameRequest(auth1.authToken(), ChessGame.TeamColor.WHITE, gameID);
        try {
            gameService.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("An unexpected exeption was thrown: " + e.getMessage());
        }
        // Then have the second user try to join as the White player
        JoinGameRequest request2 = new JoinGameRequest(auth2.authToken(), ChessGame.TeamColor.WHITE, gameID);
        try {
            gameService.joinGame(request2);
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.TAKEN, e.statusCode());
        }

        // Check that the first user is still the White player
        ListGamesResult listGamesResult = null;
        try {
            listGamesResult = gameService.listGames(new ListGamesRequest(auth1.authToken()));
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }
        ArrayList<GameHeader> gameList = new ArrayList<>(listGamesResult.games());
        GameHeader game = gameList.getFirst();
        Assertions.assertEquals(game.whiteUsername(), user1.username(), "White username incorrectly changed");
    }

    @Test
    public void joinGameUnauthorized() {
        // Register a user, log them in, and create a game
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);
        int gameID = testFactory.createGame(auth.authToken(), "New game");

        // Attempt to join a game using a bogus authToken
        JoinGameRequest request = new JoinGameRequest("invalidAuth", null, gameID);
        try {
            gameService.joinGame(request);
            Assertions.fail("Did not throw Exception when given invalid authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(ResponseException.StatusCode.UNAUTHORIZED, e.statusCode());
        }
    }

}
