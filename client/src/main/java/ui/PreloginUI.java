package ui;

import exception.ResponseException;
import model.AuthData;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import serverFacade.ServerFacade;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class PreloginUI extends UI {
    private final ServerFacade serverFacade;
    private final PostloginUI postloginUI;
    public PreloginUI(String serverDomain, int serverPort) {
        serverFacade = new ServerFacade(serverDomain, serverPort);
        postloginUI = new PostloginUI(serverFacade);
    }

    public void run() {
        // display welcome message
        System.out.print(SET_TEXT_COLOR_MAGENTA + SET_TEXT_BOLD);
        System.out.println(WHITE_KING + " Welcome to Chess!" + WHITE_KING);
        System.out.print(SET_TEXT_COLOR_WHITE + RESET_TEXT_BOLD_FAINT);

        // Client REPL loop. Stay in this loop until the user uses the "quit" command to exit the program.
        help();
        replLoop();
        System.out.println("Thank you for coming!");
    }

    private void replLoop() {
        boolean loop = true;
        while (loop) {
            switch (getCommand()) {
                case "quit" -> loop = false;
                case "login" -> login();
                case "register" -> register();
                default -> help();
            }
        }
    }

    private void help() {
        // Print out all available actions with descriptions of what they do
        System.out.println("Available commands:");
        System.out.println("help        display available commands");
        System.out.println("quit        exit the program");
        System.out.println("login       login to chess with your username and password");
        System.out.println("register    create a new username and password to play chess");
    }

    private void login() {
        // Prompt user for login information, if needed
        LoginRequest loginRequest = getLoginInformation();

        // Attempt to log the user in
        LoginResult loginResult;
        try {
            loginResult = serverFacade.login(loginRequest);
        } catch (ResponseException e) {
            if (e.statusCode() == ResponseException.StatusCode.UNAUTHORIZED) {
                printErrorMessage("Invalid username or password. You may try again.");
            } else {
                printErrorMessage("Login attempt was unsuccessful. You may try again.");
            }
            return;
        }

        // Transition to the postLogin UI
        postloginUI.run(new AuthData(loginResult.username(), loginResult.authToken()));
        help();
    }

    private void register() {
        // Prompt user for registration information
        RegisterRequest registerRequest = getRetistrationInformation();

        // Attempt to register the user with the server (which also logs them in)
        RegisterResult registerResult;
        try {
            registerResult = serverFacade.register(registerRequest);
        } catch (ResponseException e) {
            switch (e.statusCode()) {
                case BAD_REQUEST -> printErrorMessage("A required field was left blank. You may try again.");
                case TAKEN -> printErrorMessage("Username already taken. You may try again.");
                default -> printErrorMessage("Registration unsuccessful. You may try again.");
            }
            return;
        }

        // Transition the postLogin UI
        postloginUI.run(new AuthData(registerResult.username(), registerResult.authToken()));
        help();
    }

    /*
     * Helper methods
     */
    private LoginRequest getLoginInformation() {
        System.out.print("Enter username: ");
        String username = new Scanner(System.in).next();
        System.out.print("Enter password: ");
        String password = new Scanner(System.in).next();
        return new LoginRequest(username, password);
    }

    private RegisterRequest getRetistrationInformation() {
        System.out.print("Enter username: ");
        String username = new Scanner(System.in).next();
        System.out.print("Enter password: ");
        String password = new Scanner(System.in).next();
        System.out.print("Enter email: ");
        String email = new Scanner(System.in).next();
        return new RegisterRequest(username, password, email);
    }
}
