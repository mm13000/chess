package handler;

import com.google.gson.Gson;
import dataAccess.auth.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.user.UserDAO;
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

    public void clearUsers() throws DataAccessException {
        userService.clearUsersAndAuths();
    }

    public void registerUser(Request httpReq, Response response) {
        UserData user = new Gson().fromJson(httpReq.body(), UserData.class);
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());
        RegisterResult result;

        try {
            result = userService.registerUser(request);
        } catch (BadRequestException e) {
            response.status(400);
            response.body(new Gson().toJson(new ErrorMessage("Error: bad request")));
            return;
        } catch (TakenException e) {
            response.status(403);
            response.body(new Gson().toJson(new ErrorMessage("Error: already taken")));
            return;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
            return;
        }

        // Everything successful. Return the auth token with status code 200
        response.status(200);
        response.body(new Gson().toJson(result));
    }

    public void login(Request httpReq, Response response) {
        LoginRequest loginRequest = new Gson().fromJson(httpReq.body(), LoginRequest.class);
        LoginResult result;

        try {
            result = userService.login(loginRequest);
        } catch (UnauthorizedException e) {
            response.status(401);
            response.body(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
            return;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
            return;
        }

        // Everything successful. Return the auth token with status code 200
        response.status(200);
        response.body(new Gson().toJson(result));
    }

    public void logout(Request httpReq, Response response) {
        LogoutRequest logoutRequest = new LogoutRequest(httpReq.headers("Authorization"));
        try {
            userService.logout(logoutRequest);
        } catch (UnauthorizedException e) {
            response.status(401);
            response.body(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
            return;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
            return;
        }
        response.status(200);
        response.body("");
    }
}
