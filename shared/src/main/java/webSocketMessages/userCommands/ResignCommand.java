package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    private final Integer gameID;
    public ResignCommand(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public Integer getGameID() {
        return gameID;
    }
}
