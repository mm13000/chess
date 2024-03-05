package serviceTests;

import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOMemory;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOMemory;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOMemory;
import model.AuthData;
import model.UserData;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import service.GameService;
import service.UserService;

public class ServiceTestFactory {
    public final AuthDAO authDAO;
    public final UserDAO userDAO;
    public final GameDAO gameDAO;
    public final UserService userService;
    public final GameService gameService;

    public ServiceTestFactory() {
        authDAO = new AuthDAOMemory();
        userDAO = new UserDAOMemory();
        gameDAO = new GameDAOMemory();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    public UserData registerUser(UserData user) {
        RegisterRequest registerRequest = new RegisterRequest(user.username(), user.password(), user.email());
        try {
            userService.registerUser(registerRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public AuthData loginUser(UserData user) {
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        LoginResult loginResult;
        try {
            loginResult = userService.login(loginRequest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return new AuthData(loginResult.username(), loginResult.authToken());
    }

    public int createGame(String authToken, String gameName) {
        // Attempt to create a valid game
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        CreateGameResult result;
        try {
            result = gameService.createGame(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result.gameID();
    }
}