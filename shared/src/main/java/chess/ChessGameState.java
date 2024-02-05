package chess;

import chess.chessRules.ChessRules;

import java.util.Objects;

public class ChessGameState {
    private ChessBoard board;
    private ChessGame.TeamColor turn;
    private ChessRules rules;

    public ChessGameState() {
        board = new ChessBoard();
        board.resetBoard();
        rules = new ChessRules();
        turn = ChessGame.TeamColor.WHITE;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessGame.TeamColor getTurn() {
        return turn;
    }

    public void setTurn(ChessGame.TeamColor turn) {
        this.turn = turn;
    }

    public ChessRules getRules() {
        return rules;
    }

    public void setRules(ChessRules rules) {
        this.rules = rules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGameState that = (ChessGameState) o;
        return Objects.equals(getBoard(), that.getBoard()) && getTurn() == that.getTurn() && Objects.equals(getRules(), that.getRules());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), getTurn(), getRules());
    }
}
