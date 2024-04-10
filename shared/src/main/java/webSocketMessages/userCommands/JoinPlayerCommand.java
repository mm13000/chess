package webSocketMessages.userCommands;

import chess.ChessGame.TeamColor;

public class JoinPlayerCommand extends UserGameCommand {
    private final Integer gameID;
    private final TeamColor playerColor;
    public JoinPlayerCommand(String authToken, Integer gameID, TeamColor team) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = team;
        this.commandType = CommandType.JOIN_PLAYER;
    }
}
