package clientTests;

import model.AuthData;
import model.UserData;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import serverFacade.ServerFacade;

public class ServerFacadeTestFactory {
    public final ServerFacade serverFacade;

    public ServerFacadeTestFactory(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public UserData registerUser(UserData user) {
        RegisterRequest registerRequest = new RegisterRequest(user.username(), user.password(), user.email());
        try {
            serverFacade.register(registerRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public AuthData loginUser(UserData user) {
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        LoginResult loginResult;
        try {
            loginResult = serverFacade.login(loginRequest);
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
            result = serverFacade.createGame(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result.gameID();
    }
}