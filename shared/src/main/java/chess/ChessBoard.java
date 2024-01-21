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
        // FIXME: Need to check if there is already a piece there?
        int col = position.getColumn();
        int row = position.getRow();
        this.chessBoard[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        if (this.chessBoard[row][col] == null) {
            return null;
        }
        return this.chessBoard[row][col];
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
        this.chessBoard[0][1] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        this.chessBoard[0][6] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        this.chessBoard[7][1] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        this.chessBoard[7][6] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        // Rooks
        this.chessBoard[0][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        this.chessBoard[0][7] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        this.chessBoard[7][0] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        this.chessBoard[7][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        // Pawns
        for (int i = 0; i < 8; i++) {
            this.chessBoard[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
            this.chessBoard[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }
    }

    @Override
    public String toString() {
        // Build output string to print board
        StringBuilder out = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                out.append("|");
                String pieceLetter;
                if (this.chessBoard[i][j] == null) {
                    pieceLetter = "   ";
                } else if (this.chessBoard[i][j].pieceColor.equals(TeamColor.WHITE)) {
                    pieceLetter = switch (this.chessBoard[i][j].type) {
                        case KING -> " K ";
                        case QUEEN -> " Q ";
                        case BISHOP -> " B ";
                        case ROOK -> " R ";
                        case KNIGHT -> " N ";
                        case PAWN -> " P ";
                    };
                } else {
                    pieceLetter = switch (this.chessBoard[i][j].type) {
                        case KING -> " k ";
                        case QUEEN -> " q ";
                        case BISHOP -> " b ";
                        case ROOK -> " r ";
                        case KNIGHT -> " n ";
                        case PAWN -> " p ";
                    };
                }
                out.append(pieceLetter);
            }
            out.append("|\n");
        }

        return out.toString();
    }

    public static void main(String[] args) {
        ChessBoard my_board = new ChessBoard();
        my_board.resetBoard();
        System.out.println(my_board);
    }
}
