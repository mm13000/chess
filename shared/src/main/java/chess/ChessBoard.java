package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        board[row - 1][col - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return board[row - 1][col - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessGame.TeamColor color = switch (row) {
                    case 1, 2 -> ChessGame.TeamColor.WHITE;
                    case 7, 8 -> ChessGame.TeamColor.BLACK;
                    default -> null;
                };
                ChessPiece.PieceType type = switch (col) {
                    case 1, 8 -> ChessPiece.PieceType.ROOK;
                    case 2, 7 -> ChessPiece.PieceType.KNIGHT;
                    case 3, 6 -> ChessPiece.PieceType.BISHOP;
                    case 4 -> ChessPiece.PieceType.QUEEN;
                    case 5 -> ChessPiece.PieceType.KING;
                    default -> null;
                };
                ChessPiece piece = switch (row) {
                    case 2, 7 -> new ChessPiece(color, ChessPiece.PieceType.PAWN);
                    case 1, 8 -> new ChessPiece(color, type);
                    default -> null;
                };
                addPiece(new ChessPosition(row, col), piece);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 8; i >= 1; i--) {
            boardString.append("|");
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = getPiece(new ChessPosition(i, j));
                if (piece == null) boardString.append(" ");
                else boardString.append(piece);
                boardString.append("|");
            }
            boardString.append("\n");
        }
        return boardString.toString();
    }
}
