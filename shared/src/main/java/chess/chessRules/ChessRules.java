package chess.chessRules;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.Collection;
import java.util.HashSet;

public class ChessRules {
    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        // If no piece in given position, no valid moves
        if (piece == null) return null;

        // Create a collection to hold any valid moves we find
        HashSet<ChessMove> validMoves = new HashSet<>();

        // Get possible moves from piece
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        // Check each of the moves to see that it will not put our team in check
        for (var possibleMove : possibleMoves) {
            // Get the piece that is in the possible move's end position
            // (we'll need to put it back since these moves are hypothetical)
            var endPositionPiece = board.getPiece(possibleMove.getEndPosition());
            // Try making the move
            board.movePiece(possibleMove);

            // Check that the king has not been put into check (or is no longer in check)
            if (!isInCheck(board, piece.getTeamColor())) {
                // Then we can add this as a valid move!
                validMoves.add(possibleMove);
            }

            // Return the board to its original state
            board.movePiece(possibleMove.reverseMove());
            board.addPiece(possibleMove.getEndPosition(), endPositionPiece);
        }

        return validMoves;
    }

    public boolean isInCheck(ChessBoard board, TeamColor teamColor) {
        // get positions of all pieces
        var pieces = board.getAllPieces();

        // First find our King's position
        ChessPosition kingPosition = null;
        for (var positionPieceEntry : pieces.entrySet()) {
            var pos = positionPieceEntry.getKey();
            var piece = positionPieceEntry.getValue();
            if (piece.getTeamColor() == teamColor && piece.getPieceType() == PieceType.KING) {
                kingPosition = pos;
                break;
            }
        }

        // If there is no King for this team, the team is not in check (just for running test cases)
        if (kingPosition == null) return false;

        // For each opposing piece, check if it could move into our King's position
        for (var positionPieceEntry : pieces.entrySet()) {
            ChessPosition pos = positionPieceEntry.getKey();
            ChessPiece piece = positionPieceEntry.getValue();
            if (piece.getTeamColor() != teamColor) {
                // Get all the moves this enemy piece could make
                Collection<ChessMove> enemyMoves = piece.pieceMoves(board, pos);
                // If any of its moves could end in our king's position, we are in check
                for (var enemyMove : enemyMoves) {
                    if (enemyMove.getEndPosition().equals(kingPosition)) return true;
                }
            }
        }

        return false;
    }

    public boolean isInCheckmate(ChessBoard board, TeamColor teamColor) {
        return isInCheck(board, teamColor) && isInStalemate(board, teamColor);
    }

    public boolean isInStalemate(ChessBoard board, TeamColor teamColor) {
        // get all pieces and positions
        var pieces = board.getAllPieces();

        // Check if any of our team's pieces can move
        for (var pos : pieces.keySet()) {
            if (board.getPiece(pos).getTeamColor() == teamColor) {
                var validMoves = validMoves(board, pos);
                if (validMoves != null && !validMoves.isEmpty()) return false;
            }
        }

        return true;
    }
}
