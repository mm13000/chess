package chess;

import chess.chessRules.ChessRules;
import com.google.gson.Gson;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    public TeamColor playerTurn;
    private final ChessRules rules;
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        rules = new ChessRules();
        playerTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getPlayerTurn() {
        return playerTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setPlayerTurn(TeamColor team) {
        playerTurn = team;
    }

    private void advancePlayerTurn() {
        playerTurn = playerTurn.equals(TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return rules.validMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean moveIsValid = false;

        // Check that it is the right team's turn, and they are attempting to move their own piece
        TeamColor moveTeam = getBoard().getPiece(move.getStartPosition()).getTeamColor();
        if (getPlayerTurn() != moveTeam) throw new InvalidMoveException("Move out of turn");

        // Get all the valid moves for the piece at the specified start position
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || validMoves.isEmpty()) {
            throw new InvalidMoveException("Position yields no valid moves");
        }

        // Check the proposed move against every valid move to see if it matches one
        // If it does, make the move
        for (var validMove : validMoves) {
            if (validMove.equals(move)) {
                board.movePiece(move);
                advancePlayerTurn();
                moveIsValid = true;
                break;
            }
        }
        // If not, throw an exception
        if (!moveIsValid) throw new InvalidMoveException("Move is not valid");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return rules.isInCheck(board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return rules.isInCheckmate(board, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return rules.isInStalemate(board, teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public static void main(String[] args) {
        record MyRecord(TeamColor teamColor) {}
        MyRecord myRecord = new MyRecord(TeamColor.WHITE);
        ChessGame game = new ChessGame();
        String json = new Gson().toJson(game);
        System.out.print(json);
    }

}
