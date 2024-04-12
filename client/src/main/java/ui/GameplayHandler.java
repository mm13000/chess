package ui;

import chess.ChessGame;

public interface GameplayHandler {
    public void updateGame(ChessGame game);
    public void printMessage(String message);
    public void printError(String errorMessage);
}
