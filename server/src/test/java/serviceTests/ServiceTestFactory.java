package serviceTests;

import dataAccess.*;
import model.UserData;
import request.RegisterRequest;
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
}
