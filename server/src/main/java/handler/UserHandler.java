package handler;

import com.google.gson.Gson;
import dataAccess.auth.AuthDAO;
import dataAccess.user.UserDAO;
import exception.ResponseException;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.*;
import spark.*;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    public void clearUsers() throws ResponseException {
        userService.clearUsersAndAuths();
    }

    public void registerUser(Request httpReq, Response response) throws ResponseException {
        UserData user = new Gson().fromJson(httpReq.body(), UserData.class);
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());
        RegisterResult result;
        result = userService.registerUser(request);

        // Everything successful. Return the auth token with status code 200
        response.status(200);
        response.body(new Gson().toJson(result));
    }

    public void login(Request httpReq, Response response) throws ResponseException {
        LoginRequest loginRequest = new Gson().fromJson(httpReq.body(), LoginRequest.class);
        LoginResult result;
        result = userService.login(loginRequest);

        // Everything successful. Return the auth token with status code 200
        response.status(200);
        response.body(new Gson().toJson(result));
    }

    public void logout(Request httpReq, Response response) throws ResponseException {
        LogoutRequest logoutRequest = new LogoutRequest(httpReq.headers("Authorization"));
        userService.logout(logoutRequest);

        response.status(200);
        response.body("");
    }
}
