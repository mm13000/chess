package chess.moveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface MoveCalc {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
