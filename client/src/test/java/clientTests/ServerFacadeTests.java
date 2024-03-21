package clientTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;
import serverFacade.ServerFacade;
import status.StatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static ServerFacadeTestFactory testFactory;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost", port);
        testFactory = new ServerFacadeTestFactory(serverFacade);
    }

    @BeforeEach
    void setup() {
        // Clear the server before each test
        AuthDAO authDAO = new AuthDAOmySQL();
        UserDAO userDAO = new UserDAOmySQL();
        GameDAO gameDAO = new GameDAOmySQL();
        try {
            gameDAO.clearGames();
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerUserNewUser() {
        RegisterRequest goodRequest = new RegisterRequest("bob1", "1234", "bob1@gmail.com");
        Assertions.assertDoesNotThrow(() -> serverFacade.register(goodRequest),
                "Unexpected exception was thrown during regular user registration");
        try {
            serverFacade.register(goodRequest);
            Assertions.fail("ResponseException not thrown in duplicate user registration");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.TAKEN, e.StatusCode());
        }
    }

    @Test
    void registerUserCorrectResult() {
        RegisterRequest goodRequest = new RegisterRequest("bob1", "1234", "bob1@gmail.com");
        RegisterResult actualResult;
        try {
            actualResult = serverFacade.register(goodRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RegisterResult expectedResult = new RegisterResult("bob1", "");
        Assertions.assertEquals(expectedResult.username(), actualResult.username(),
                "registerUser() did not return the correct username result");
        Assertions.assertDoesNotThrow(() -> UUID.fromString(actualResult.authToken()),
                "registerUser() did not return a valid AuthToken");
    }

    @Test
    void registerUserBadRequest() {
        RegisterRequest badRequest = new RegisterRequest("bob1", null, "bob1@gmail.com");
        try {
            serverFacade.register(badRequest);
            Assertions.fail("Response exception not thrown with null password at user registration");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.BAD_REQUEST, e.StatusCode());
        }
    }

    /*
     * Tests of UserService.login()
     */

    @Test
    void loginExistingUser() {
        // First register a user so that the user exists in the server
        var user = testFactory.registerUser(new UserData("bob","pass","bob@me.com"));
        // Then send a valid login request
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        try {
            LoginResult result = serverFacade.login(loginRequest);
            // Test that the LoginResult we receive has correct username and a valid authToken
            Assertions.assertEquals(user.username(), result.username(), "Incorrect username returned");
            Assertions.assertDoesNotThrow(() -> UUID.fromString(result.authToken()), "Invalid UUID returned");
        } catch (Exception e) {
            Assertions.fail("Test failed because an exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void LoginWrongPassword() {
        // First register a user so that a user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then send a login request with the wrong password
        LoginRequest loginRequest = new LoginRequest(user.username(), "wrongPassword");
        // an "UnauthorizedException" should be thrown
        try {
            serverFacade.login(loginRequest);
            Assertions.fail("Response exception not thrown at login with incorrect password");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        }
    }

    @Test
    void LoginNonexistentUser() {
        // First register a user so that a user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then send a login request for a nonexistent user
        LoginRequest loginRequest = new LoginRequest("mallory", user.password());
        // an "UnauthorizedException" should be thrown
        try {
            serverFacade.login(loginRequest);
            Assertions.fail("Response exception not thrown at login of nonexistent user");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        }
    }

    @Test
    void LoginBadRequest() {
        // Bad request because of null username
        LoginRequest nullUsername = new LoginRequest(null, "1234");
        try {
            serverFacade.login(nullUsername);
            Assertions.fail("ResponseException not thrown at login with null username");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.ERROR, e.StatusCode());
        }

        // Bad request because of null password
        LoginRequest nullPassword = new LoginRequest("bob", null);
        try {
            serverFacade.login(nullPassword);
            Assertions.fail("ResponseException not thrown at login with null password");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.ERROR, e.StatusCode());
        }

        // Bad request because of empty username
        LoginRequest emptyUsername = new LoginRequest("", "1234");
        try {
            serverFacade.login(emptyUsername);
            Assertions.fail("ResponseException not thrown at login with empty username");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.ERROR, e.StatusCode());
        }

        // Bad request because of empty password
        LoginRequest emptyPassword = new LoginRequest("bob", "");
        try {
            serverFacade.login(emptyPassword);
            Assertions.fail("ResponseException not thrown at login with empty password");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.ERROR, e.StatusCode());
        }
    }

    /*
     * Tests of UserService.logout()
     */

    @Test
    void logoutExistingUser() {
        // First register a user so that the user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then login with that user to get an authToken
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        LoginResult loginResult;
        try {
            loginResult = serverFacade.login(loginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Then send a logout request with that authToken
        LogoutRequest request = new LogoutRequest(loginResult.authToken());
        try {
            serverFacade.logout(request);
        } catch (Exception e) {
            Assertions.fail("Test failed because an exception was thrown: " + e.getMessage());
        }
        // Then ensure that the authToken is no longer valid
        try {
            serverFacade.logout(request);
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        } catch (Exception e) {
            Assertions.fail("Test failed because an exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void logoutNonexistentAuth() {
        // Try logging someone out when there is no one yet stored in the database
        try {
            serverFacade.logout(new LogoutRequest("nonexistentAuthToken"));
            Assertions.fail("No ResponseException thrown when logging out with nonexistent authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        }
        // First register a user so that the user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then login with that user to get an authToken
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        try {
            serverFacade.login(loginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Then send a logout request with a nonexistent authToken
        try {
            serverFacade.logout(new LogoutRequest("nonexistentAuthToken"));
            Assertions.fail("No ResponseException thrown when logging out with nonexistent authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        }
    }

    @Test
    void logoutBadRequest() {
        // Bad request because of null authToken
        LogoutRequest nullAuthToken = new LogoutRequest(null);
        try {
            serverFacade.logout(nullAuthToken);
            Assertions.fail("No ResponseException thrown when logging out with null authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.ERROR, e.StatusCode());
        }
        // Bad request because of empty authToken
        LogoutRequest emptyAuthToken = new LogoutRequest("");
        try {
            serverFacade.logout(emptyAuthToken);
            Assertions.fail("No ResponseException thrown when logging out with empty authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.ERROR, e.StatusCode());
        }
    }

    @Test
    public void createValidGame() {
        // Register a user and log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        AuthData auth = testFactory.loginUser(user);

        // Then attempt to create a valid game
        CreateGameRequest request = new CreateGameRequest(auth.authToken(), "New Game!!");
        CreateGameResult result = null;
        try {
            result = serverFacade.createGame(request);
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
            result1 = serverFacade.createGame(request1);
            result2 = serverFacade.createGame(request2);
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

        // Assert that in each case a BadRequestException is thrown
        try {
            serverFacade.createGame(badRequest1);
            Assertions.fail("ResponseException not thrown with null gameName");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.BAD_REQUEST, e.StatusCode());
        }
        try {
            serverFacade.createGame(badRequest2);
            Assertions.fail("ResponseException not thrown with empty gameName");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.BAD_REQUEST, e.StatusCode());
        }
        try {
            serverFacade.createGame(badRequest3);
            Assertions.fail("ResponseException not thrown with null authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.BAD_REQUEST, e.StatusCode());
        }
        try {
            serverFacade.createGame(badRequest4);
            Assertions.fail("ResponseException not thrown with empty authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.BAD_REQUEST, e.StatusCode());
        }
    }

    @Test
    public void createGameUnauthorized() {
        // Attempt to create a game with no valid users in database
        CreateGameRequest request = new CreateGameRequest("fakeAuth", "Try Me!");
        try {
            serverFacade.createGame(request);
            Assertions.fail("ResponseException not thrown with no registered users");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        }

        // Register a user and log them in
        UserData user = testFactory.registerUser(new UserData("bob","1234", "bob@me.com"));
        testFactory.loginUser(user);

        // Attempt to create a game using fake authToken
        try {
            serverFacade.createGame(request);
            Assertions.fail("ResponseException not thrown with invalid Auth Token");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
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
            result = serverFacade.listGames(request);
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
            serverFacade.listGames(request);
            Assertions.fail("ResponseException not thrown with invalid authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
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
            serverFacade.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }
        // Attempt to join game as Black player (player playing themselves)
        request = new JoinGameRequest(auth.authToken(), ChessGame.TeamColor.BLACK, gameID);
        try {
            serverFacade.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("Failed because exception was thrown: " + e.getMessage());
        }

        // Check that the game has been updated in the database
        ListGamesResult listGamesResult = null;
        try {
            listGamesResult = serverFacade.listGames(new ListGamesRequest(auth.authToken()));
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
            serverFacade.joinGame(request);
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
            serverFacade.joinGame(request);
            Assertions.fail("Did not throw ResponseException with invalid gameID");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.BAD_REQUEST, e.StatusCode());
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
            serverFacade.joinGame(request);
        } catch (Exception e) {
            Assertions.fail("An unexpected exeption was thrown: " + e.getMessage());
        }
        // Then have the second user try to join as the White player
        JoinGameRequest request2 = new JoinGameRequest(auth2.authToken(), ChessGame.TeamColor.WHITE, gameID);
        try {
            serverFacade.joinGame(request2);
            Assertions.fail("ResponseException not thrown when attempting to join as White Player");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.TAKEN, e.StatusCode());
        }

        // Check that the first user is still the White player
        ListGamesResult listGamesResult = null;
        try {
            listGamesResult = serverFacade.listGames(new ListGamesRequest(auth1.authToken()));
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
            serverFacade.joinGame(request);
            Assertions.fail("ResponseException not thrown with invalid authToken");
        } catch (ResponseException e) {
            Assertions.assertEquals(StatusCode.UNAUTHORIZED, e.StatusCode());
        }
    }
}
