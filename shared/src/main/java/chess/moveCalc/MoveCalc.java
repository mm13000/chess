package chess.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class MoveCalc {
    /*
    This is a super class for each type of chess move
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        return switch (board.chessBoard[row][col].type) {
            case KING -> KingCalc.pieceMoves(board, myPosition);
            case QUEEN -> QueenCalc.pieceMoves(board, myPosition);
            case BISHOP -> BishopCalc.pieceMoves(board, myPosition);
            case KNIGHT -> KnightCalc.pieceMoves(board, myPosition);
            case ROOK -> RookCalc.pieceMoves(board, myPosition);
            case PAWN -> PawnCalc.pieceMoves(board, myPosition);
            case null -> null;
        };
    }
}
