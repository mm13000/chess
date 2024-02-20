package service;

import dataAccess.UserDAO;
import dataAccess.UserDAOMemory;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOMemory();
    }

    public void clearUsers() {
        userDAO.clearUsers();
    }
}
