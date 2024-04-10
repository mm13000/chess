package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
    private final Integer gameID;
    public JoinObserverCommand(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
