package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clearUsersAndAuths() {
        userDAO.clearUsers();
        authDAO.clearAuths();
    }

    public RegisterResult registerUser(RegisterRequest request) throws BadRequestException, NameTakenException, DataAccessException {
        // first check that the request is valid
        if (request.username() == null || request.email() == null || request.password() == null
                || request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("One of the required fields was null or empty");
        }
        // then check that the username is not already taken
        try {
            userDAO.getUser(request.username());
            throw new NameTakenException("Username taken");
        } catch (DataAccessException ignored) {}

        // then create a user object and add that user to the database
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.addUser(user);

        // Then create a new Auth for the user and add it to the database
        AuthData auth = authDAO.newAuth(request.username());

        return new RegisterResult(user.username(), auth.authToken());
    }
}
