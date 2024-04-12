package webSocketMessages.userCommands;

import chess.ChessGame.TeamColor;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final Integer gameID;
    private final ChessMove move;
    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }

}
