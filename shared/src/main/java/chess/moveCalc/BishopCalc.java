package chess.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;

import java.util.Collection;
import java.util.HashSet;

public class BishopCalc extends MoveCalc {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        HashSet<ChessMove> moves = new HashSet<>(); // Stores the valid moves we calculate

        // True until out of bounds, encounters friendly piece, or encounters opponent piece
        boolean can_continue = true;

        for (int k = 0; k < 4; k++) {
            // 4 iterations: NW, NE, SE, SW
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (can_continue) {
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
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null) {
                    can_continue = false; // FIXME
                }
            }
        }

        // Traverse the NW diagonal

        // Traverse the NE diagonal
        can_continue = true;
        while (can_continue) {

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
