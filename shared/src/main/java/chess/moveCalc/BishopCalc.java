package chess.moveCalc;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class BishopCalc extends MoveCalc {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        // Get basic info about my piece
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // Stores the valid moves we calculate
        HashSet<ChessMove> moves = new HashSet<>();

        // 4 iterations for each diagonal direction: NW, NE, SE, SW
        for (int k = 0; k < 4; k++) {
            // At each iteration, start where the Bishop is located
            int row = myRow;
            int col = myCol;
            while (true) {
                // Adjust the row and column by one step
                row = switch (k) {
                    case 0, 1 -> row + 1;
                    case 2, 3 -> row - 1;
                    default -> throw new IllegalStateException("Unexpected value: " + k);
                };
                col = switch (k) {
                    case 0, 3 -> col - 1;
                    case 1, 2 -> col + 1;
                    default -> throw new IllegalStateException("Unexpected value: " + k);
                };
                ChessPosition newPosition = new ChessPosition(row, col);
                // First check that we have not gone out of bounds
                if (ChessBoard.invalidPosition(newPosition)) {
                    break;
                }
                // get the ChessPiece and ChessMove at the new location we are exploring
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                ChessMove move = new ChessMove(myPosition, new ChessPosition(row, col));
                if (piece == null) {
                    // If there is no piece there, it is a valid move:
                    moves.add(move);
                } else if (piece.teamColor.equals(myPiece.teamColor)) {
                    // If one of my team's pieces is there, not valid move and cannot continue searching this direction
                    break;
                } else {
                    // If one of opponent's pieces is there, valid move but also cannot continue to look this direction
                    moves.add(move);
                    break;
                }
            }
        }

        return moves;
        /*
        Valid moves are anything on the diagonal. Includes killing opponent piece. Cannot pass/get blocked by team
        piece. Start where bishop is located. Work in different directions, one move at a time, adding to the 'moves'
        set as I go.

        Maybe while loops
        */
    }
}
