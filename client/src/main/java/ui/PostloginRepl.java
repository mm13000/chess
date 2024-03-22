package ui;

import exception.ResponseException;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.GameHeader;
import result.ListGamesResult;
import serverFacade.ServerFacade;
import status.StatusCode;
import static ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        // Prompt user for game name
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

        // Display a list of available games, or a message that no games are available
        if (listGamesResult.games().isEmpty()) {
            printNotification("There are no games available. You can create a new game!");
        } else {
            printGameList(listGamesResult.games());
        }
    }

    private void joinGame() {

    }

    private void observeGame() {

    }

    /*
     * Helper methods
     */

    private void printGameList(List<GameHeader> gameList) {
        // Names of columns
        List<String> colNames = Arrays.asList("Game ID", "Game Name", "White Player", "Black Player");

        // calculate column widths (maximums of each column)
        List<Integer> colWidths = new ArrayList<>();
        for (String colName : colNames) {
            colWidths.add(colName.length());
        }
        for (GameHeader gameHeader : gameList) {
            colWidths.set(0, Math.max(colWidths.get(0), String.valueOf(gameHeader.gameID()).length()));
            colWidths.set(1, Math.max(colWidths.get(1), gameHeader.gameName().length()));

            int whiteUsernameLength = gameHeader.whiteUsername() == null ? 0 : gameHeader.whiteUsername().length();
            colWidths.set(2, Math.max(colWidths.get(2), whiteUsernameLength));
            int blackUsernameLength = gameHeader.blackUsername() == null ? 0 : gameHeader.blackUsername().length();
            colWidths.set(3, Math.max(colWidths.get(3), blackUsernameLength));
        }

        // Create a column formatting string for the table with the proper column widths
        StringBuilder builder = new StringBuilder();
        builder.append("|");
        for (int colWidth : colWidths ) {
            builder.append(" %-").append(colWidth).append("s |");
        }
        builder.append("%n");
        String formattedRow = builder.toString();

        // Print the table header
        System.out.println(tableSeparator(colWidths));
        System.out.format(formattedRow, colNames.get(0), colNames.get(1), colNames.get(2), colNames.get(3));
        System.out.println(tableSeparator(colWidths));

        // Print the body of the table, and a final separator at the end to close the table
        for (GameHeader game : gameList) {
            String whiteUsername = game.whiteUsername() == null ? "" : game.whiteUsername();
            String blackUsername = game.blackUsername() == null ? "" : game.blackUsername();
            System.out.format(formattedRow, game.gameID(), game.gameName(), whiteUsername, blackUsername);
        }
        System.out.println(tableSeparator(colWidths));
    }

    private String tableSeparator(List<Integer> colWidths) {
        StringBuilder separator = new StringBuilder();
        separator.append("+");
        for (int i = 0; i < 4; i++) {
            int width = colWidths.get(i) + 2;
            separator.append("-".repeat(Math.max(0, width)));
            separator.append("+");
        }
        return separator.toString();
    }

//    private List<Integer> calculateColumnWidths(List<String> colNames, List<GameHeader> gameList) {
//        List<Integer> colWidths = new ArrayList<>();
//        for (String colName : colNames) {
//            colWidths.add(colName.length());
//        }
//        for (GameHeader gameHeader : gameList) {
//            colWidths.set(0, Math.max(colWidths.get(0), String.valueOf(gameHeader.gameID()).length()));
//            colWidths.set(1, Math.max(colWidths.get(1), gameHeader.gameName().length()));
//            int whiteUsernameLength = gameHeader.whiteUsername() == null ? 0 : gameHeader.whiteUsername().length();
//            colWidths.set(2, Math.max(colWidths.get(2), whiteUsernameLength));
//            int blackUsernameLength = gameHeader.blackUsername() == null ? 0 : gameHeader.blackUsername().length();
//            colWidths.set(3, Math.max(colWidths.get(3), blackUsernameLength));
//        }
//        return colWidths;
//    }
//
//    private String buildTableFormat(List<Integer> colWidths) {
//        StringBuilder builder = new StringBuilder("|");
//        for (int colWidth : colWidths) {
//            builder.append(" %-").append(colWidth).append("s |");
//        }
//        builder.append("%n");
//        return builder.toString();
//    }
//
//    private String formatTableRow(List<Integer> colWidths) {
//        StringBuilder separator = new StringBuilder("+");
//        for (Integer colWidth : colWidths) {
//            int width = colWidth + 2;
//            separator.append("-".repeat(Math.max(0, width))).append("+");
//        }
//        return separator.toString();
//    }
//
//    private void printGameList(List<GameHeader> gameList) {
//        List<String> colNames = Arrays.asList("Game ID", "Game Name", "White Player", "Black Player");
//        List<Integer> colWidths = calculateColumnWidths(colNames, gameList);
//
//        String formattedRow = buildTableFormat(colWidths);
//        String tableSeparator = formatTableRow(colWidths);
//
//        System.out.println(tableSeparator);
//        System.out.format(formattedRow, colNames.get(0), colNames.get(1), colNames.get(2), colNames.get(3));
//        System.out.println(tableSeparator);
//
//        for (GameHeader game : gameList) {
//            String whiteUsername = game.whiteUsername() == null ? "" : game.whiteUsername();
//            String blackUsername = game.blackUsername() == null ? "" : game.blackUsername();
//            System.out.format(formattedRow, game.gameID(), game.gameName(), whiteUsername, blackUsername);
//        }
//        System.out.println(tableSeparator);
//    }

}
