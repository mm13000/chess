package chess.chessRules.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

public class PawnCalc implements MoveCalc{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(myPosition);
        boolean forward1Unoccupied = false; // flag for use in for loop below

        // 4 iterations for 4 possible moves: {0: forward 1, 1: capture left, 2: capture right, 3: forward 2}

        for (int k = 0; k < 4; k++) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            int reverse = switch (myPiece.getTeamColor()) {
                case WHITE -> 1;
                case BLACK -> -1;
            };
            row = switch (k) {
                case 0, 1, 2 -> row + reverse;
                case 3 -> row + 2 * reverse;
                default -> row;
            };
            col = switch (k) {
                case 1 -> col - reverse;
                case 2 -> col + reverse;
                default -> col;
            };
            if (row < 1 || row > 8 || col < 1 || col > 8) continue; // if out of bounds go to next iteration/position
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove possibleMove = new ChessMove(myPosition, newPosition);
            ChessPiece occupyingPiece = board.getPiece(newPosition);

            // check if the move is possible / allowed
            boolean movePossible = false;
            switch (k) {
                case 0:
                    if (occupyingPiece == null) {
                        movePossible = true;
                        forward1Unoccupied = true;
                    }
                    break;
                case 1, 2:
                    if (occupyingPiece != null && !occupyingPiece.getTeamColor().equals(myPiece.getTeamColor())) {
                        movePossible = true;
                    }
                    break;
                case 3:
                    boolean canMove2Forward = switch (myPiece.getTeamColor()) {
                        case WHITE -> myPosition.getRow() == 2;
                        case BLACK -> myPosition.getRow() == 7;
                    };
                    if (canMove2Forward && forward1Unoccupied && occupyingPiece == null) {
                        movePossible = true;
                    }
                    break;
            }

            // Take care of promotions
            if (movePossible) {
                boolean promotion = switch (myPiece.getTeamColor()) {
                    case WHITE -> row == 8;
                    case BLACK -> row == 1;
                };
                if (promotion) {
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                } else {
                    moves.add(possibleMove);
                }
            }
        }

        return moves;
    }
}
