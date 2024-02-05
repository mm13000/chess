package chess.chessRules.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KnightCalc implements MoveCalc{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        // 8 iterations for 8 directions:
        // 0: left 2, up 1
        // 1: left 1, up 2
        // 2: right 1, up 2
        // 3: right 2, up 1
        // 4: right 2, down 1
        // 5: right 1, down 2
        // 6: left 1, down 2
        // 7: left 2, down 1
        for (int k = 0; k < 8; k++) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            row = switch (k) {
                case 0, 7 -> row - 2;
                case 1, 6 -> row - 1;
                case 2, 5 -> row + 1;
                case 3, 4 -> row + 2;
                default -> row;
            };
            col = switch (k) {
                case 0, 3 -> col + 1;
                case 1, 2 -> col + 2;
                case 4, 7 -> col - 1;
                case 5, 6 -> col - 2;
                default -> col;
            };
            if (row < 1 || row > 8 || col < 1 || col > 8) continue; // if out of bounds go to next iteration/position
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove possibleMove = new ChessMove(myPosition, newPosition);
            ChessPiece occupyingPiece = board.getPiece(newPosition);
            if (occupyingPiece == null || !occupyingPiece.getTeamColor().equals(myPiece.getTeamColor())) {
                moves.add(possibleMove);
            } // if no one there or opposite team, add move but don't break
        }
        return moves;
    }
}
