package handler;

import dataAccess.UserDAO;
import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler() {
        this.userService = new UserService();
    }

    public void clearUsers() {
        userService.clearUsers();
    }
}
