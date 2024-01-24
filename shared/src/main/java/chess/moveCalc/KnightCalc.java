package chess.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KnightCalc extends MoveCalc {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // basic piece info
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // set to store valid moves we calculate
        HashSet<ChessMove> moves = new HashSet<>();

        // 8 possible moves. Start from 0: (left 2, up 1) and move clockwise in a circle of possible moves
        for (int k = 0; k < 8; k++) {
            int row = myRow;
            int col = myCol;
            row = switch (k) {
                case 4, 7 -> row - 1;
                case 0, 3 -> row + 1;
                case 5, 6 -> row - 2;
                case 1, 2 -> row + 2;
                default -> row;
            };
            col = switch (k) {
                case 1, 6 -> col - 1;
                case 2, 5 -> col + 1;
                case 0, 7 -> col - 2;
                case 3, 4 -> col + 2;
                default -> col;
            };
            ChessPosition newPosition = new ChessPosition(row, col);
            // Check that we are not out of bounds
            if (ChessBoard.invalidPosition(newPosition)) continue;
            // Get ChessPiece at that position, and corresponding (possible) ChessMove
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            ChessMove move = new ChessMove(myPosition, new ChessPosition(row, col));
            // Check the possible move, add to 'moves' set if applicable, stop iterating if needed
            if (piece == null) {
                moves.add(move);
            } else if (!piece.teamColor.equals(myPiece.teamColor)) {
                moves.add(move);
            }
        }

        return moves;
    }
}
