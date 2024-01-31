package chess.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class KingCalc implements MoveCalc {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        // 8 iterations for 8 directions: {0: N, 1: NE, 2: E, 3: SE, 4: S, 5: SW, 6: W, 7: NW}
        for (int k = 0; k < 8; k++) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            row = switch (k) {
                case 0, 1, 7 -> row + 1;
                case 3, 4, 5 -> row - 1;
                default -> row;
            };
            col = switch (k) {
                case 1, 2, 3 -> col + 1;
                case 5, 6, 7 -> col - 1;
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
