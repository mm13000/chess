package chess.chessRules;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiPredicate;

public class ChessRules {
    public Collection<ChessMove> validMoves(ChessBoard board, ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        // Create a collection to hold any valid moves we find
        HashSet<ChessMove> validMoves = new HashSet<>();

        // Get possible moves from piece's calculator
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        for (var possibleMove : possibleMoves) {
            // Try making the move
            board.movePiece(possibleMove);

            // Check that the king has not been put into check (or is no longer in check)
            if (!isInCheck(board, piece.getTeamColor())) {
                // Then we can add this as a valid move!
                validMoves.add(possibleMove);
            }

            // Always return the board to its original state
            board.movePiece(possibleMove.ReverseMove());
        }

        return validMoves;
    }

    private ChessPosition getPiecePosition(ChessBoard board, BiPredicate<ChessPiece, TeamColor> condition) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (condition.test(piece, piece.getTeamColor())) {
                    return position;
                }
            }
        }
        return null;
    }

    public boolean isInCheck(ChessBoard board, TeamColor teamColor) {
        // get our King's position
        BiPredicate<ChessPiece, TeamColor> kingCondition = (piece, team) -> piece.getPieceType() == PieceType.KING && piece.getTeamColor().equals(team);
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                }
            }
        }

        // Get positions of all pieces on the opposing team
        HashSet<ChessPosition> enemyPositions = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                // If we found an opposing piece
                if (piece != null && piece.getTeamColor() != teamColor) {
                    // Get all the moves this piece could make
                    Collection<ChessMove> enemyMoves = piece.pieceMoves(board, pos);
                    // If any of its moves could end in our king's position, we are in check
                    for (var enemyMove : enemyMoves) {
                        if (enemyMove.getEndPosition().equals(kingPosition)) return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(ChessBoard board, TeamColor teamColor) {
        return isInCheck(board, teamColor) && isInStalemate(board, teamColor);
    }

    public boolean isInStalemate(ChessBoard board, TeamColor teamColor) {
        boolean noValidMoves = true;
//        // See if there is any position on the board, matching this team's color, with a valid move available
//        BiPredicate<ChessPiece, TeamColor> condition = (board, pos) -> {
//            return validMoves(board, );
//        };
        // Iterate through the entire board
        outerLoop:
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                // At each position, check whether there are any valid moves
                ChessPosition pos = new ChessPosition(i, j);
                // If there are any valid moves, then n
                if (validMoves(board, pos) != null) {
                    noValidMoves = false;
                    break outerLoop;
                }
            }
        }
        return noValidMoves;
    }
}
