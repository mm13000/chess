package chess.moveCalc;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class PawnCalc extends MoveCalc {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // basic piece info
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // Pawns can only move one direction. If Black, they move opposite direction
        int reverse = switch (myPiece.getTeamColor()) {
            case WHITE -> 1; // don't reverse direction of travel
            case BLACK -> -1; // do reverse direction of travel
        };

        // set to store valid moves we calculate
        HashSet<ChessMove> moves = new HashSet<>();

        // Four possible moves
        //  0: forward one space (only if unoccupied)
        //  1: forward and diagonal left one space (only if occupied by opponent piece)
        //  2: forward and diagonal right one space (only if occupied by opponent piece)
        //  3: forward two spaces (only if unoccupied and pawn is in its original position)
        for (int k = 0; k < 4; k++) {
            int row = myRow;
            int col = myCol;
            // On 3rd iteration, first check that pawn is in original position (original row):
            if (k == 3) {
                int origRow = switch (myPiece.getTeamColor()) {
                    case WHITE -> 2;
                    case BLACK -> 7;
                };
                if (myRow != origRow) {
                    break;
                }
            }
            // Increment row and column as appropriate, get new position
            row = switch (k) {
                case 0, 1, 2 -> row + reverse;
                case 3 -> row + 2 * reverse;
                default -> row;
            };
            col = switch (k) {
                case 0, 3 -> col;
                case 1 -> col - reverse;
                case 2 -> col + reverse;
                default -> col;
            };
            ChessPosition newPosition = new ChessPosition(row, col);

            // Check that we are not out of bounds
            if (ChessBoard.invalidPosition(newPosition)) continue;

            // Get ChessPiece at that position, and corresponding (possible) ChessMove
            ChessPiece piece = board.getPiece(newPosition);

            // Get (possible) ChessMoves (with all promotion possibilities if applicable)
            HashSet<ChessMove> possible_moves = new HashSet<>();
            int otherSideRow = switch (myPiece.getTeamColor()) {
                case WHITE -> 8;
                case BLACK -> 1;
            };
            if (row == otherSideRow) {
                possible_moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                possible_moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                possible_moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                possible_moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
            } else {
                possible_moves.add(new ChessMove(myPosition, newPosition));
            }

            // On iteration 0, check that there is no piece in the proposed new position:
            if (k == 0 && piece == null) {
                moves.addAll(possible_moves);
            }
            // On iteration 3, check that there is no piece in the new position OR in the in between spot
            if (k == 3) {
                ChessPiece inBetweenPiece = board.getPiece(new ChessPosition(row - reverse, col));
                if (piece == null && inBetweenPiece == null) {
                    moves.addAll(possible_moves);
                }
            }
            // otherwise, check that there is an opponent piece in the proposed position
            else if ((k == 1 || k == 2) && piece != null && !myPiece.getTeamColor().equals(piece.getTeamColor())) {
                moves.addAll(possible_moves);
            }
        }

        return moves;
    }
}
