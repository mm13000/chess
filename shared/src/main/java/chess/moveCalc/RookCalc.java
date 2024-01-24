package chess.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class RookCalc extends MoveCalc {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // basic piece info
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // set to store valid moves we calculate
        HashSet<ChessMove> moves = new HashSet<>();

        // Four iterations to search {0: up, 1: down, 2: left, 3: right}
        for (int k = 0; k < 4; k++) {
            // At each iteration, start where the Rook is located
            int row = myRow;
            int col = myCol;
            while (true) {
                // Adjust the row or column by one step to move up, down, left, or right
                row = switch (k) {
                    case 0 -> row + 1;
                    case 1 -> row - 1;
                    default -> row;
                };
                col = switch (k) {
                    case 2 -> col - 1;
                    case 3 -> col + 1;
                    default -> col;
                };
                ChessPosition newPosition = new ChessPosition(row, col);
                // Check that we are not out of bounds
                if (ChessBoard.invalidPosition(newPosition)) break;
                // Get ChessPiece at that position, and corresponding (possible) ChessMove
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                ChessMove move = new ChessMove(myPosition, new ChessPosition(row, col));
                // Check the possible move, add to 'moves' set if applicable, stop iterating if needed
                if (piece == null) {
                    moves.add(move);
                } else if (piece.teamColor.equals(myPiece.teamColor)) {
                    break;
                } else {
                    moves.add(move);
                    break;
                }
            }
        }

        return moves;
    }
}
