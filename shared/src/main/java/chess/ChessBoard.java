package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessPiece[][] chessBoard;

    public ChessBoard() {
        this.chessBoard = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Kings
        this.chessBoard[0][4] = new ChessPiece(TeamColor.WHITE, PieceType.KING);
        this.chessBoard[7][4] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        // Queens
        this.chessBoard[0][3] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        this.chessBoard[7][3] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        // Bishops
        this.chessBoard[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        this.chessBoard[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        this.chessBoard[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        this.chessBoard[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        // Knights
        this.chessBoard[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        this.chessBoard[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        this.chessBoard[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        this.chessBoard[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        // Rooks
        this.chessBoard[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        this.chessBoard[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        this.chessBoard[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        this.chessBoard[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        // Pawns
        for (int i = 0; i < 8; i++) {
            this.chessBoard[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
            this.chessBoard[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }
    }
}
