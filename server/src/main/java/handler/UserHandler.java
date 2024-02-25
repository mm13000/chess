package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;
import service.*;
import spark.*;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    public void clearUsers() {
        userService.clearUsersAndAuths();
    }

    public Response registerUser(Request httpReq, Response response) {
        UserData user = new Gson().fromJson(httpReq.body(), UserData.class);
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());
        RegisterResult result;

        try {
            result = userService.registerUser(request);
        } catch (BadRequestException e) {
            response.status(400);
            response.body(new Gson().toJson(new ErrorMessage("Error: bad request")));
            return response;
        } catch (NameTakenException e) {
            response.status(403);
            response.body(new Gson().toJson(new ErrorMessage("Error: already taken")));
            return response;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage("Error: " + e.getMessage())));
            return response;
        }

        // Everything successful. Return the auth token with status code 200
        response.status(200);
        response.body(new Gson().toJson(result));
        return response;
    }
}
