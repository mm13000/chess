package ui;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessPiece;
import chess.ChessPosition;
import serverFacade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.*;

public class GameplayRepl extends Repl {
    private final ServerFacade serverFacade;
    private String authToken;
    private ChessBoard board;
    public GameplayRepl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void run(String authToken) {
        this.authToken = authToken;

        // Welcome message
        printNotification("\nWelcome to the game!\n");

        // Retrieve the game from the server
        // FIXME: HOW TO DO THIS?
        board = new ChessBoard();
        board.resetBoard();

        // Print the board in both orientations
        printGameBoard(TeamColor.BLACK);
        printGameBoard(TeamColor.WHITE);

        // Notify user of return to log in state
        printNotification("You have left gameplay.");
    }

    /*
     * Helper methods
     */

    /*
     * Helper methods specific to printing the board
     */

    private void printGameBoard(TeamColor orientation) {
        // Print top row (letters)
        printBoardHeader(orientation);

        // Print main body of the board
        printBoardRows(orientation);

        // Print bottom row (letters)
        printBoardHeader(orientation);

        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
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

        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + SET_BG_COLOR_WHITE);
        System.out.print("\n");
    }

    private void printBoardRows(TeamColor orientation) {
        // Print the board rows in the order appropriate depending on chess board orientation
        List<Integer> rows = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        if (orientation.equals(TeamColor.WHITE)) rows = rows.reversed();
        for (Integer row : rows) {
            printBoardRowNumber(row);
            printBoardRow(row, orientation);
            printBoardRowNumber(row);
            System.out.print("\n");
        }
    }

    private void printBoardRowNumber(Integer number) {
        System.out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE);
        printBoardCell(number.toString());
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + SET_BG_COLOR_WHITE);
    }

    private void printBoardRow(Integer row, TeamColor orientation) {
        // Print a row of the board (just the pieces)
        List<Integer> cols = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        if (orientation.equals(TeamColor.BLACK)) cols = cols.reversed();
        for (Integer col : cols) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            printBoardPiece(row, col, piece);
        }
    }

    private void printBoardPiece(Integer row, Integer col, ChessPiece piece) {
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

        // Print the piece
        printBoardCell(pieceChar);

        // Reset coloring
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + SET_BG_COLOR_WHITE);
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
