package service;

import dataAccess.auth.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.user.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public void clearUsersAndAuths() throws ResponseException {
        try {
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.ERROR, e.getMessage());
        }
    }

    public RegisterResult registerUser(RegisterRequest request) throws ResponseException {
        // first check that the request is valid
        if (invalidRequest(request)) {
            throw new ResponseException(ResponseException.StatusCode.BAD_REQUEST, "One of the required fields was null or empty");
        }
        // then check that the username is not already taken
        try {
            userDAO.getUser(request.username());
            throw new ResponseException(ResponseException.StatusCode.TAKEN, "Username taken");
        } catch (DataAccessException ignored) {}

        // then create a user object and add that user to the database
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(request.password());
        UserData user = new UserData(request.username(), hashedPassword, request.email());
        try {
            userDAO.addUser(user);
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.ERROR, e.getMessage());
        }

        // Then create a new Auth for the user and add it to the database
        AuthData auth;
        try {
            auth = authDAO.newAuth(request.username());
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.ERROR, e.getMessage());
        }

        return new RegisterResult(user.username(), auth.authToken());
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        // first check that the request is valid
        if (invalidRequest(request)) {
            throw new ResponseException(ResponseException.StatusCode.BAD_REQUEST, "One of the required fields was null or empty");
        }
        // then check that a user exists with the provided username
        UserData user;
        try {
            user = userDAO.getUser(request.username());
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.UNAUTHORIZED, "No user with that username");
        }
        // then check that the password is correct
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.password(), user.password())) {
            throw new ResponseException(ResponseException.StatusCode.UNAUTHORIZED, "Incorrect password");
        }
        // then create a new Auth for the user and add it to the database
        AuthData auth;
        try {
            auth = authDAO.newAuth(request.username());
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.ERROR, e.getMessage());
        }

        return new LoginResult(user.username(), auth.authToken());
    }

    public void logout(LogoutRequest request) throws ResponseException {
        // first check that the request is valid (required fields have been provided)
        if (invalidRequest(request)) {
            throw new ResponseException(ResponseException.StatusCode.BAD_REQUEST, "No authToken provided");
        }
        // then check that the authToken is valid
        try {
            authDAO.getAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.UNAUTHORIZED, "Invalid authToken");
        }
        // then delete the authToken from the database
        try {
            authDAO.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(ResponseException.StatusCode.ERROR, e.getMessage());
        }
    }

    public String getUsername(String authToken) {
        // Returns null for the username if authToken is invalid
        try {
            return authDAO.getAuth(authToken).username();
        } catch (DataAccessException e) {
            return null;
        }
    }
}
