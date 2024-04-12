package chess.chessRules.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class RookCalc implements MoveCalc{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        // 4 iterations for 4 directions: {0: N, 1: E, 2: S, 3: W}
        for (int k = 0; k < 4; k++) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (true) {
                row = switch (k) {
                    case 0 -> row + 1;
                    case 2 -> row - 1;
                    default -> row;
                };
                col = switch (k) {
                    case 1 -> col + 1;
                    case 3 -> col - 1;
                    default -> col;
                };
                if (checkOccupantAndAddMove(board, myPosition, moves, myPiece, row, col))
                    break; // if out of bounds, break while loop, start again
            }
        }
        return moves;
    }

    static boolean checkOccupantAndAddMove(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, ChessPiece myPiece, int row, int col) {
        if (row < 1 || row > 8 || col < 1 || col > 8) return true;
        ChessPosition newPosition = new ChessPosition(row, col);
        ChessMove possibleMove = new ChessMove(myPosition, newPosition);
        ChessPiece occupyingPiece = board.getPiece(newPosition);
        if (occupyingPiece == null) moves.add(possibleMove); // if no one there, add move but don't break
        else if (occupyingPiece.getTeamColor().equals(myPiece.getTeamColor())) return true;
        else {
            moves.add(possibleMove); // if opposite team, add move and break
            return true;
        }
        return false;
    }
}
