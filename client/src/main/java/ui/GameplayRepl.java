package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import serverFacade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Map;

public class GameplayRepl extends Repl {
    private final ServerFacade serverFacade;
    private String authToken;
    public GameplayRepl(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void run(String authToken) {
        this.authToken = authToken;

        // Welcome message
        printNotification("\nWelcome to the game!\n");

        // Retrieve the game from the server
        // FIXME: HOW TO DO THIS?
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        // Print the board in both orientations
        printGameBoard(board);

        // Notify user of return to log in state
        printNotification("You have left gameplay.");
    }

    /*
     * Helper methods
     */

    private void printGameBoard(ChessBoard board) {
        Map<ChessPosition, ChessPiece> chessPieces = board.getAllPieces();

        printBoard(chessPieces, false);

        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
    }

    private void printBoard(Map<ChessPosition, ChessPiece> chessPieces, boolean reverse) {
        printBoardSection(chessPieces, 0, 9, 1);
        printBoardSection(chessPieces, 9, 0, -1);
        System.out.print(SET_BG_COLOR_WHITE + "\n");
    }

    private void printBoardSection(Map<ChessPosition, ChessPiece> chessPieces, int start, int end, int step) {
        for (int i = start; i != end + step; i += step) {
            for (int j = 0; j <= 9; j++) {
                printCell(i, j, chessPieces);
            }
            System.out.print(SET_BG_COLOR_WHITE + "\n");
        }
    }

    private void printCell(int i, int j, Map<ChessPosition, ChessPiece> chessPieces) {
        if (i == 9 || i == 0) {
            printRowHeader(j);
        } else if (j == 0 || j == 9) {
            printColumnHeader(i);
        } else {
            printBoardCell(i, j, chessPieces);
        }
    }

    private void printRowHeader(int j) {
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD);
        String character = switch (j) {
            case 1 -> "h";
            case 2 -> "g";
            case 3 -> "f";
            case 4 -> "e";
            case 5 -> "d";
            case 6 -> "c";
            case 7 -> "b";
            case 8 -> "a";
            default -> " ";
        };
        System.out.print(" " + character + " ");
    }

    private void printColumnHeader(int i) {
        System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD);
        String character = i <= 8 && i >= 1 ? String.valueOf(i) : " ";
        System.out.print(" " + character + " ");
    }

    private void printBoardCell(int i, int j, Map<ChessPosition, ChessPiece> chessPieces) {
        System.out.print((i % 2 == j % 2) ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY);
        ChessPiece piece = chessPieces.get(new ChessPosition(i, j));
        if (piece == null) {
            System.out.print("   ");
        } else {
            System.out.print(SET_TEXT_BOLD);
            System.out.print((piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE);
            System.out.print(" " + piece + " ");
        }
    }
}
