package chess;

import chess.chessRules.moveCalc.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (board.getPiece(myPosition).getPieceType()) {
            case KING -> new KingCalc().pieceMoves(board, myPosition);
            case QUEEN -> new QueenCalc().pieceMoves(board, myPosition);
            case BISHOP -> new BishopCalc().pieceMoves(board, myPosition);
            case KNIGHT -> new KnightCalc().pieceMoves(board, myPosition);
            case ROOK -> new RookCalc().pieceMoves(board, myPosition);
            case PAWN -> new PawnCalc().pieceMoves(board, myPosition);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return getTeamColor() == that.getTeamColor() && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamColor(), getPieceType());
    }

    @Override
    public String toString() {
        if (this.teamColor.equals(ChessGame.TeamColor.WHITE)) {
            return switch (this.pieceType) {
                case PAWN -> "P";
                case ROOK -> "R";
                case KING -> "K";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KNIGHT -> "N";
            };
        } else return switch (this.pieceType) {
            case KING -> "k";
            case QUEEN -> "q";
            case BISHOP -> "b";
            case KNIGHT -> "n";
            case ROOK -> "r";
            case PAWN -> "p";
        };
    }
}
