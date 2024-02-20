package service;

import dataAccess.AuthDAO;
import dataAccess.AuthDAOMemory;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService() {
        this.authDAO = new AuthDAOMemory();
    }

    public void clearAuths() {
        authDAO.clearAuths();
    }
}
