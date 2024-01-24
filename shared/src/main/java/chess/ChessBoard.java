package chess;

import chess.ChessPiece.PieceType;
import chess.ChessGame.TeamColor;

import java.util.Arrays;
import java.util.Collection;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] chessBoard;

    public ChessBoard() {
        this.chessBoard = new ChessPiece[8][8];
    }

    /*
     * Check whether a position is a valid location on the board
     */
    public static boolean invalidPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row < 1 || row > 8 || col < 1 || col > 8;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        // TODO: Need to check if there is already a piece there?
        if (invalidPosition(position)) {
            return;
        }
        int row = position.getRow();
        int col = position.getColumn();
        this.chessBoard[row - 1][col - 1] = piece;
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
        if (invalidPosition(position)) {
            return null;
        } else if (this.chessBoard[row - 1][col - 1] == null) {
            return null;
        }
        return this.chessBoard[row - 1][col - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Kings
        addPiece(new ChessPosition(1,5), new ChessPiece(TeamColor.WHITE, PieceType.KING));
        addPiece(new ChessPosition(8,5), new ChessPiece(TeamColor.BLACK, PieceType.KING));
        // Queens
        addPiece(new ChessPosition(1,4), new ChessPiece(TeamColor.WHITE, PieceType.QUEEN));
        addPiece(new ChessPosition(8,4), new ChessPiece(TeamColor.BLACK, PieceType.QUEEN));
        // Bishops
        addPiece(new ChessPosition(1,3), new ChessPiece(TeamColor.WHITE, PieceType.BISHOP));
        addPiece(new ChessPosition(1,6), new ChessPiece(TeamColor.WHITE, PieceType.BISHOP));
        addPiece(new ChessPosition(8,3), new ChessPiece(TeamColor.BLACK, PieceType.BISHOP));
        addPiece(new ChessPosition(8,6), new ChessPiece(TeamColor.BLACK, PieceType.BISHOP));
        // Knights
        addPiece(new ChessPosition(1,2), new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT));
        addPiece(new ChessPosition(1,7), new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT));
        addPiece(new ChessPosition(8,2), new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT));
        addPiece(new ChessPosition(8,7), new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT));
        // Rooks
        addPiece(new ChessPosition(1,1), new ChessPiece(TeamColor.WHITE, PieceType.ROOK));
        addPiece(new ChessPosition(1,8), new ChessPiece(TeamColor.WHITE, PieceType.ROOK));
        addPiece(new ChessPosition(8,1), new ChessPiece(TeamColor.BLACK, PieceType.ROOK));
        addPiece(new ChessPosition(8,8), new ChessPiece(TeamColor.BLACK, PieceType.ROOK));
        // Pawns
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(TeamColor.WHITE, PieceType.PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(TeamColor.BLACK, PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }

    @Override
    public String toString() {
        // Build output string to print board
        StringBuilder out = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j <= 7; j++) {
                out.append("|");
                String pieceLetter;
                if (this.chessBoard[i][j] == null) {
                    pieceLetter = "   ";
                } else if (this.chessBoard[i][j].teamColor.equals(TeamColor.WHITE)) {
                    pieceLetter = switch (this.chessBoard[i][j].pieceType) {
                        case KING -> " K ";
                        case QUEEN -> " Q ";
                        case BISHOP -> " B ";
                        case ROOK -> " R ";
                        case KNIGHT -> " N ";
                        case PAWN -> " P ";
                    };
                } else {
                    pieceLetter = switch (this.chessBoard[i][j].pieceType) {
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
//        my_board.resetBoard();
//        System.out.println(my_board);
        ChessPiece myPiece = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        ChessPosition myPosition = new ChessPosition(5,4);
        my_board.addPiece(myPosition, myPiece);
        Collection<ChessMove> moves = myPiece.pieceMoves(my_board, myPosition);
    }
}
