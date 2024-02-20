package handler;

import service.AuthService;

public class AuthHandler {
    private final AuthService authService;

    public AuthHandler() {
        this.authService = new AuthService();
    }

    public void clearAuths() {
        authService.clearAuths();
    }
}
