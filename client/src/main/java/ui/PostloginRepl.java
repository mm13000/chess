package ui;

import exception.ResponseException;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.ListGamesResult;
import serverFacade.ServerFacade;
import status.StatusCode;

import java.util.Scanner;

public class PostloginRepl extends Repl {
    private String authToken = null;
    private final ServerFacade serverFacade;
    public PostloginRepl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void run(String authToken) {
        this.authToken = authToken;
        printNotification("\nYou are logged in.\n");

        // display available commands
        help();

        // Main REPL loop. Stay in this loop until the user uses the "logout" command to return to the previous repl.
        boolean loop = true;
        while (loop) {
            // get user command from the terminal
            System.out.print("\nEnter command: ");
            String userCommand = new Scanner(System.in).next();
            System.out.print("\n");

            // Call the appropriate method
            switch (userCommand) {
                case "logout" -> loop = false;
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                default -> help();
            }
        }
        logout();
    }

    private void help() {
        // Print out all available actions with descriptions of what they do
        System.out.println("Available commands:");
        System.out.println("help        display available commands");
        System.out.println("logout");
        System.out.println("create      create a new chess game");
        System.out.println("list        list available chess games");
        System.out.println("join        join a chess game");
        System.out.println("observe     observe a chess game");
    }

    private void logout() {
        // Attempt to log the user out
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        try {
            serverFacade.logout(logoutRequest);
        } catch (ResponseException e) {
            if (e.StatusCode() == StatusCode.UNAUTHORIZED) {
                printErrorMessage("Unauthorized logout error.");
            } else {
                printErrorMessage("Error attempting to logout.");
            }
            return;
        }

        // Notify user of successful logout
        printNotification("\nYou have been logged out.");
    }

    private void createGame() {

    }

    private void listGames() {
        // Attempt to list the games found in the Server
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult;
        try {
            listGamesResult = serverFacade.listGames(listGamesRequest);
        } catch (ResponseException e) {
            if (e.StatusCode() == StatusCode.UNAUTHORIZED) {
                printErrorMessage("Unauthorized list games request.");
            } else {
                printErrorMessage("Error attempting to list games.");
            }
            return;
        }

        // Display a list of available games
        System.out.println("Available games:");
        for (var game : listGamesResult.games()) {
            // FIXME: Implement this. Can I make it a table?
        }
    }

    private void joinGame() {

    }

    private void observeGame() {

    }

    /*
     * Helper methods
     */
}
