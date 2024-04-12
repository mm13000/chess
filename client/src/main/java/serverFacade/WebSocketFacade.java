package serverFacade;

import chess.ChessGame.TeamColor;
import chess.ChessMove;
import com.google.gson.Gson;
import model.AuthData;
import ui.GameplayHandler;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private final GameplayHandler gameplayHandler;
    private final AuthData authData;
    public WebSocketFacade(GameplayHandler gameplayHandler, AuthData authData) {
        this.gameplayHandler = gameplayHandler;
        this.authData = authData;
        setupWebSocket();
    }

    /*
     * Methods for sending requests/commands to the server
     */

    public void joinPlayer(Integer gameID, TeamColor team) {
        JoinPlayerCommand command = new JoinPlayerCommand(authData.authToken(), gameID, team);
        sendMessage(new Gson().toJson(command));
    }

    public void joinObserver(Integer gameID) {
        JoinObserverCommand command = new JoinObserverCommand(authData.authToken(), gameID);
        sendMessage(new Gson().toJson(command));
    }

    public void makeMove(Integer gameID, ChessMove move) {
        MakeMoveCommand command = new MakeMoveCommand(authData.authToken(), gameID, move);
        sendMessage(new Gson().toJson(command));
    }

    public void leaveGame(Integer gameID) {
        LeaveCommand command = new LeaveCommand(authData.authToken(), gameID);
        sendMessage(new Gson().toJson(command));
    }

    public void resign(String authToken, Integer gameID) {
        ResignCommand command = new ResignCommand(authToken, gameID);
        sendMessage(new Gson().toJson(command));
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Methods for handling incoming messages from the server
     */

    @Override
    public void onMessage(String serverMessageJson) {
        // First deserialize the message as a ServerMessage to retrieve the message type
        ServerMessage message = new Gson().fromJson(serverMessageJson, ServerMessage.class);
        // Then send the message to the correct handler based on its type
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> onLoadGameMessage(serverMessageJson);
            case NOTIFICATION -> onNotificationMessage(serverMessageJson);
            case ERROR -> onErrorMessage(serverMessageJson);
        }
    }

    private void onLoadGameMessage(String loadGameMessageJson) {
        // Deserialize LoadGameMessage, then send it to the gameplay handler
        LoadGameMessage message = new Gson().fromJson(loadGameMessageJson, LoadGameMessage.class);
        gameplayHandler.updateGame(message.getGame());
    }

    private void onNotificationMessage(String notificationMessageJson) {
        // Deserialize the NotificationMessage, then send it to the gameplay handler
        NotificationMessage message = new Gson().fromJson(notificationMessageJson, NotificationMessage.class);
        gameplayHandler.printMessage(message.getMessage());
    }

    private void onErrorMessage(String errorMessageJson) {
        // Deserialize the NotificationMessage, then send it to the gameplay handler
        ErrorMessage message = new Gson().fromJson(errorMessageJson, ErrorMessage.class);
        gameplayHandler.printError(message.getErrorMessage());
    }

    /*
     * Setup methods
     */

    private void setupWebSocket() {
        // Connect to the server and get a WebSocket session
        try {
            URI uri = new URI("ws://localhost:8080/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }

        // Register the message handler class (this class, since it implements MessageHandler.Whole<>)
        this.session.addMessageHandler(this);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
