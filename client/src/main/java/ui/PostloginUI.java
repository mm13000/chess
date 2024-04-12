package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.CreateGameResult;
import result.GameHeader;
import result.ListGamesResult;
import serverFacade.ServerFacade;

import java.util.*;

public class PostloginUI extends UI {
    private AuthData authData = null;
    private final ServerFacade serverFacade;
    private final GameplayUI gameplayUI;
    private final HashMap<Integer, Integer> gameDisplayOrder = new HashMap<>();
    public PostloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.gameplayUI = new GameplayUI();
    }

    public void run(AuthData authData) {
        this.authData = authData;
        printNotification("\nYou are logged in.\n");

        initializeGameDisplayMap(); // initialize gameDisplayOrder Map

        // Client REPL loop. Stay in this loop until the user uses the "logout" command to return to the previous repl.
        help();
        replLoop();
    }

    private void initializeGameDisplayMap() {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authData.authToken());
        ListGamesResult listGamesResult = null;
        try {
            listGamesResult = this.serverFacade.listGames(listGamesRequest);
        } catch (ResponseException ignored) {}
        // Assign new numbers for each of the games in the list (starting at 1)
        int gameNum = 1;
        for (GameHeader gameHeader : listGamesResult.games()) {
            gameDisplayOrder.put(gameHeader.gameID(), gameNum++);
        }
    }

    private void replLoop() {
        boolean loop = true;
        while (loop) {
            switch (getCommand()) {
                case "logout" -> {
                    loop = false;
                    logout();
                }
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                default -> help();
            }
        }
    }

    private void help() {
        // Print out all available actions with descriptions of what they do
        System.out.println("Available commands:");
        System.out.println("help        display available commands");
        System.out.println("logout      logout");
        System.out.println("create      create a new chess game");
        System.out.println("list        list available chess games");
        System.out.println("join        join a chess game");
        System.out.println("observe     observe a chess game");
    }

    private void logout() {
        // Attempt to log the user out
        LogoutRequest logoutRequest = new LogoutRequest(authData.authToken());
        try {
            serverFacade.logout(logoutRequest);
        } catch (ResponseException e) {
            if (e.StatusCode() == ResponseException.StatusCode.UNAUTHORIZED) {
                printErrorMessage("Server threw an unauthorized logout error.");
            } else {
                printErrorMessage("Error in attempting to logout.");
            }
            return;
        }

        // Notify user of successful logout
        printNotification("You have been logged out.");
    }

    private void createGame() {
        // Prompt user for game request information
        CreateGameRequest createGameRequest = getCreateGameInfo();

        // Attempt to create the game in the server
        CreateGameResult createGameResult;
        try {
            createGameResult = serverFacade.createGame(createGameRequest);
        } catch (ResponseException e) {
            if (e.StatusCode() == ResponseException.StatusCode.BAD_REQUEST) {
                printErrorMessage("Game name or number invalid. Please try again.");
            } else {
                printErrorMessage("Error creating game. You may try again");
            }
            return;
        }

        // Notify successful completion, including new game ID
        printNotification("Your new chess game '" + createGameRequest.gameName() + "' has been created!");
    }

    private void listGames() {
        // Attempt to list the games found in the Server
        ListGamesRequest listGamesRequest = new ListGamesRequest(authData.authToken());
        ListGamesResult listGamesResult;
        try {
            listGamesResult = serverFacade.listGames(listGamesRequest);
        } catch (ResponseException e) {
            if (e.StatusCode() == ResponseException.StatusCode.UNAUTHORIZED) {
                printErrorMessage("Unauthorized list games request.");
            } else {
                printErrorMessage("Error attempting to list games.");
            }
            return;
        }

        // Assign new numbers for each of the games in the list (starting at 1)
        int gameNum = 1;
        for (GameHeader gameHeader : listGamesResult.games()) {
            gameDisplayOrder.put(gameHeader.gameID(), gameNum++);
        }

        // Display a list of available games, or a message that no games are available
        if (listGamesResult.games().isEmpty()) {
            printNotification("There are no games available. You can create a new game!");
        } else {
            printGameList(listGamesResult.games());
        }
    }

    private void joinGame() {
        // Prompt user for JoinGameRequest info
        JoinGameRequest joinGameRequest = getJoinGameInfo();

        // Attempt to join the game on the server
        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (ResponseException e) {
            if (e.StatusCode() == ResponseException.StatusCode.TAKEN) {
                printErrorMessage("The team color requested is already taken by another player. You may try again.");
            } else {
                printErrorMessage("Error when attempting to join game. You may try again.");
            }
            return;
        }

        // Transition to the GamePlay UI
        gameplayUI.run(authData, joinGameRequest.gameID(), joinGameRequest.playerColor());
        help();
    }

    private void observeGame() {
        // Prompt user for JoinGameRequest info
        JoinGameRequest joinGameRequest = getObserveGameInfo();

        // Send join game request to verify that the game exists on the server
        try {
            serverFacade.joinGame(joinGameRequest);
        } catch (ResponseException e) {
            printErrorMessage("Error when attempting to observe game. You may try again.");
            return;
        }

        // Transition to the GamePlay UI
        gameplayUI.run(authData, joinGameRequest.gameID(), null);
        help();
    }

    /*
     * Helper methods
     */

    private CreateGameRequest getCreateGameInfo() {
        System.out.print("Enter game name: ");
        String gameName = new Scanner(System.in).nextLine();
        return new CreateGameRequest(authData.authToken(), gameName);
    }

    private int getGameID() {
        // Get game number:
        System.out.print("Enter Game Number: ");
        int gameNum = new Scanner(System.in).nextInt();
        int gameID = 0;
        for (int key : gameDisplayOrder.keySet()) {
            if (gameDisplayOrder.get(key).equals(gameNum)) {
                gameID = key;
            }
        }
        return gameID;
    }

    private JoinGameRequest getJoinGameInfo() {
        // Get game ID:
        int gameID = getGameID();

        // Get team color
        ChessGame.TeamColor playerColor = null;
        boolean validColorEntered = false;
        while (!validColorEntered) {
            System.out.print("Enter desired team color (white/black): ");
            String color = new Scanner(System.in).next();
            switch (color.toLowerCase()) {
                case "white":
                    playerColor = ChessGame.TeamColor.WHITE;
                    validColorEntered = true;
                    break;
                case "black":
                    playerColor = ChessGame.TeamColor.BLACK;
                    validColorEntered = true;
                    break;
                default:
                    printErrorMessage("Invalid team color. Please try again.");
            }
        }

        // return JoinGameRequest
        return new JoinGameRequest(authData.authToken(), playerColor, gameID);
    }

    private JoinGameRequest getObserveGameInfo() {
        // Get game ID:
        int gameID = getGameID();

        // Return the appropriate JoinGameRequest
        return new JoinGameRequest(authData.authToken(), null, gameID);
    }

    private void printGameList(List<GameHeader> gameList) {
        // Names of columns
        List<String> colNames = Arrays.asList("Game", "Game Name", "White Player", "Black Player");

        // calculate column widths (maximums of each column)
        List<Integer> colWidths = new ArrayList<>();
        for (String colName : colNames) {
            colWidths.add(colName.length());
        }
        for (GameHeader gameHeader : gameList) {
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
            int gameNum = gameDisplayOrder.get(game.gameID());
            String whiteUsername = game.whiteUsername() == null ? "" : game.whiteUsername();
            String blackUsername = game.blackUsername() == null ? "" : game.blackUsername();
            System.out.format(formattedRow, gameNum, game.gameName(), whiteUsername, blackUsername);
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

}
