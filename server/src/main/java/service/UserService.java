package service;

import dataAccess.auth.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.user.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

public class UserService extends Service {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clearUsersAndAuths() throws DataAccessException {
        authDAO.clearAuths();
        userDAO.clearUsers();
    }

    public RegisterResult registerUser(RegisterRequest request) throws BadRequestException, TakenException, DataAccessException {
        // first check that the request is valid
        if (invalidRequest(request)) {
            throw new BadRequestException("One of the required fields was null or empty");
        }
        // then check that the username is not already taken
        try {
            userDAO.getUser(request.username());
            throw new TakenException("Username taken");
        } catch (DataAccessException ignored) {}

        // then create a user object and add that user to the database
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.addUser(user);

        // Then create a new Auth for the user and add it to the database
        AuthData auth = authDAO.newAuth(request.username());

        return new RegisterResult(user.username(), auth.authToken());
    }

    public LoginResult login(LoginRequest request) throws UnauthorizedException, DataAccessException, BadRequestException {
        // first check that the request is valid
        if (invalidRequest(request)) {
            throw new BadRequestException("One of the required fields was null or empty");
        }
        // then check that a user exists with the provided username
        UserData user;
        try {
            user = userDAO.getUser(request.username());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No user with that username");
        }
        // then check that the password is correct
        if (!user.password().equals(request.password())) {
            throw new UnauthorizedException("Incorrect password");
        }
        // then create a new Auth for the user and add it to the database
        AuthData auth = authDAO.newAuth(request.username());

        return new LoginResult(user.username(), auth.authToken());
    }

    public void logout(LogoutRequest request) throws DataAccessException, UnauthorizedException, BadRequestException {
        // first check that the request is valid (required fields have been provided)
        if (invalidRequest(request)) {
            throw new BadRequestException("No authToken provided");
        }
        // then check that the authToken is valid
        try {
            authDAO.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid authToken");
        }
        // then delete the authToken from the database
        authDAO.deleteAuth(request.authToken());
    }
}
