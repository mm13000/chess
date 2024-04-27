package ui;

import chess.*;
import chess.ChessGame.TeamColor;
import model.AuthData;
import serverFacade.ServerFacade;
import serverFacade.WebSocketFacade;

import static ui.EscapeSequences.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameplayUI extends UI implements GameplayHandler {
    private WebSocketFacade webSocketFacade = null;
    private AuthData authData;
    private TeamColor playerTeam = null;
    private Integer gameID;
    private ChessGame game;

    public void run(AuthData authData, Integer gameID, TeamColor playerTeam) {
        this.authData = authData;
        this.gameID = gameID;
        this.playerTeam = playerTeam;
        setupWebSocketFacade();

        // Welcome message
        printNotification("\nWelcome to the game!\n");

        // Start REPL loop. Stay in this loop until user submits "leave" command"
        replLoop();
        printNotification("You have left gameplay.");
    }

    private void setupWebSocketFacade() {
        this.webSocketFacade = new WebSocketFacade(this, authData);
        if (playerTeam != null) {
            webSocketFacade.joinPlayer(gameID, playerTeam);
        } else {
            webSocketFacade.joinObserver(gameID);
        }
    }

    private void replLoop() {
        boolean loop = true;
        while (loop) {
            switch (getCommand()) {
                case "redraw" -> drawBoard(null);
                case "leave" -> {
                    loop = false;
                    leaveGame();
                }
                case "move" -> {
                    if (playerTeam != null) makeMove();
                    else help();
                }
                case "resign" -> {
                    if (playerTeam != null) resign();
                    else help();
                }
                case "show" -> displayLegalMoves();
                default -> help();
            }
        }
    }

    private void help() {
        // Print out all available actions with descriptions of what they do
        System.out.println("Available commands:");
        System.out.println("help        display available commands");
        System.out.println("redraw      redraw the chess board");
        System.out.println("leave       leave the game");
        if (playerTeam != null) System.out.println("move        move a piece");
        if (playerTeam != null) System.out.println("resign      forfeit the game");
        System.out.println("show        display all legal moves for a piece");
    }

    private void makeMove() {
        // Allow user to input what move they want to make.
        // The board is updated to reflect the result of the move,
        // and the board automatically updates on all the clients involved in the game
        ChessMove move = getMove();
        webSocketFacade.makeMove(gameID, move);
    }

    private void resign() {
        // Prompts the user to confirm they want to resign.
        // If they do, the user forfeits the game and the game is over.
        // Does not cause the user to leave the game.
        if (getResignationConfirmation()) {
            webSocketFacade.resign(authData.authToken(), gameID);
        }
    }

    private void displayLegalMoves() {
        // Allows the user to input what piece for which they want to highlight legal moves.
        // The selected piece’s current square and all squares it can legally move to are highlighted.
        // This is a local operation and has no effect on remote users’ screens.

        // Get the location of the piece for which we want to display valid moves
        System.out.print("Enter the position of the piece (e.g. 'E5'): ");
        ChessPosition piecePosition = getPosition();

        // Check that there is a piece at the position
        if (game.getBoard().getPiece(piecePosition) == null) {
            printErrorMessage("No piece at that position. You may try again.");
            return;
        }

        // Get a collection of valid moves for that piece
        Collection<ChessMove> validMoves = game.validMoves(piecePosition);
        Collection<ChessPosition> highlightPositions = new HashSet<>();
        for (var move : validMoves) {
            highlightPositions.add(move.getEndPosition());
        }
        if (highlightPositions.isEmpty()) {
            printNotification("No available moves");
        }
        drawBoard(highlightPositions);
    }

    private void leaveGame() {
        // Removes the user from the game (whether they are playing or observing the game).
        // The client transitions back to the Post-Login UI.
        webSocketFacade.leaveGame(gameID);
    }

    /*
     * Helper methods
     */

    private ChessMove getMove() {
        // Get start position
        System.out.print("Move piece FROM (e.g. 'E5'): ");
        ChessPosition startPosition = getPosition();

        // Check that there is a piece in the start position, prompt again
        while (game.getBoard().getPiece(startPosition) == null) {
            System.out.print("No piece at given position. Try again: ");
            startPosition = getPosition();
        }

        System.out.print("Move piece TO: ");
        ChessPosition endPosition = getPosition();

        // If the move makes the piece eligible for promotion, get the desired promotion type
        ChessPiece.PieceType promotionType = getPromotionType(new ChessMove(startPosition, endPosition));

        return new ChessMove(startPosition, endPosition, promotionType);
    }

    private ChessPosition getPosition() {
        // Get the position from the user
        String userEntry = new Scanner(System.in).next().toUpperCase();

        // Verify entry and ask for re-entry until a valid position is entered
        List<Character> validCols = Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        List<Character> validRows = Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8');
        while (!validCols.contains(userEntry.charAt(0)) || !validRows.contains(userEntry.charAt(1))) {
            System.out.print("Position does not exist. Try again: ");
            userEntry = new Scanner(System.in).next().toUpperCase();
        }

        // Return the valid position
        int col = validCols.indexOf(userEntry.charAt(0)) + 1;
        int row = validRows.indexOf(userEntry.charAt(1)) + 1;
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType getPromotionType(ChessMove move) {
        // Check that the piece is eligible for a promotion
        Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
        if (validMoves == null) return null;
        Collection<String> promotionTypes = new ArrayList<>();
        for (var validMove : validMoves) {
            if (validMove.getPromotionPiece() != null && validMove.getEndPosition().equals(move.getEndPosition())) {
                promotionTypes.add(validMove.getPromotionPiece().toString());
            }
        }
        if (promotionTypes.isEmpty()) return null;

        // A promotion is indeed possible!

        // Prompt for the promotion type
        System.out.print("This move makes the pawn eligible for a promotion! " +
                "If you wish to promote it, enter the type of piece you wish to promote it to ( ");
        for (String type : promotionTypes) System.out.print(type + " ");
        System.out.print("): ");

        // Get the promotion type and return it
        String userEntry = new Scanner(System.in).next().toUpperCase();
        while (!promotionTypes.contains(userEntry)) {
            System.out.print("Invalid piece type. Try again: ");
            userEntry = new Scanner(System.in).next().toUpperCase();
        }
        return ChessPiece.PieceType.valueOf(userEntry);
    }

    private boolean getResignationConfirmation() {
        System.out.print("Are you sure you want to resign? (enter Y/N): ");
        String userEntry = new Scanner(System.in).next().toUpperCase();
        while (!userEntry.equals("Y") && !userEntry.equals("N")) {
            System.out.print("Please enter 'Y' or 'N': ");
            userEntry = new Scanner(System.in).next().toUpperCase();
        }
        return userEntry.equals("Y");
    }

    /*
     * Implementations of GameplayHandler methods.
     * These handle asynchronous events that occur when the server sends a message
     */

    @Override
    public void updateGame(ChessGame game) {
        this.game = game;
        drawBoard(null);
        System.out.print("Enter command: ");
    }

    @Override
    public void printMessage(String message) {
        printNotification("\n" + message);
        System.out.print("Enter command: ");
    }

    @Override
    public void printError(String errorMessage) {
        printErrorMessage("\n" + errorMessage);
        System.out.print("Enter command: ");
    }

    /*
     * Board Printing methods
     */

    private void drawBoard(Collection<ChessPosition> highlightedPositions) {
        TeamColor orientation = playerTeam == null ? TeamColor.WHITE : playerTeam;

        // Print top row (letters)
        System.out.print("\n");
        printBoardHeader(orientation);

        // Print main body of the board
        printBoardRows(orientation, highlightedPositions);

        // Print bottom row (letters)
        printBoardHeader(orientation);
        System.out.print("\n");

        System.out.print(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE);
    }

    private void printBoardHeader(TeamColor orientation) {
        // Print the board header (or footer) with the letters in the appropriate order depending on orientation
        System.out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE);

        List<String> headerCharacters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H");
        if (orientation.equals(TeamColor.BLACK)) headerCharacters = headerCharacters.reversed();

        printBoardCell(null);
        for (String character : headerCharacters) {
            printBoardCell(character);
        }
        printBoardCell(null);

        System.out.print(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE + RESET_BG_COLOR);
        System.out.print("\n");
    }

    private void printBoardRows(TeamColor orientation, Collection<ChessPosition> highlightedPositions) {
        // Print the board rows in the order appropriate depending on chess board orientation
        List<Integer> rows = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        if (orientation.equals(TeamColor.WHITE)) rows = rows.reversed();
        for (Integer row : rows) {
            printBoardRowNumber(row);
            printBoardRow(row, orientation, highlightedPositions);
            printBoardRowNumber(row);
            System.out.print("\n");
        }
    }

    private void printBoardRowNumber(Integer number) {
        System.out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE);
        printBoardCell(number.toString());
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private void printBoardRow(Integer row, TeamColor orientation, Collection<ChessPosition> highlightedPositions) {
        // Print a row of the board (just the pieces)
        List<Integer> cols = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        if (orientation.equals(TeamColor.BLACK)) cols = cols.reversed();
        for (Integer col : cols) {
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = game.getBoard().getPiece(position);
            boolean highlight = highlightedPositions != null && highlightedPositions.contains(position);
            printBoardPiece(row, col, piece, highlight);
        }
    }

    private void printBoardPiece(Integer row, Integer col, ChessPiece piece, boolean highlight) {
        // Get the character for the appropriate piece
        TeamColor team = null;
        if (piece != null) team = piece.getTeamColor();
        String pieceChar = piece == null ? null : switch (piece.getPieceType()) {
            case KING -> team.equals(TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            case QUEEN -> team.equals(TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> team.equals(TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> team.equals(TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> team.equals(TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> team.equals(TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        };

        // Set the background color and text color appropriately
        if (piece != null) System.out.print(team.equals(TeamColor.WHITE) ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK);
        System.out.print((row + col) % 2 == 0 ? SET_TEXT_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY);
        if (highlight) System.out.print(SET_BG_COLOR_YELLOW);

        // Print the piece
        printBoardCell(pieceChar);

        // Reset coloring
        System.out.print(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_WHITE + RESET_BG_COLOR);
    }

    private void printBoardCell(String character) {
        List<String> wideCharacters = Arrays.asList(WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK,
                WHITE_PAWN, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN);
        String cellString;
        if (character == null) {
            cellString = " " + " " + EMPTY;
        } else if (wideCharacters.contains(character)) {
            cellString = " " + character + " ";
        } else {
            cellString = " " + character + EMPTY;
        }
        System.out.print(cellString);
    }
}
