package webSocketMessages.serverMessages;

import chess.ChessGameState;

public class LoadGameMessage extends ServerMessage {
    private final ChessGameState game;
    public LoadGameMessage(ChessGameState game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
