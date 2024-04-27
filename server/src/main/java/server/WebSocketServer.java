package server;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import model.GameData;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Map;

@WebSocket
public class WebSocketServer {
    private final UserService userService;
    private final GameService gameService;
    private final WebSocketSessions webSocketSessions = new WebSocketSessions();
    public WebSocketServer() {
        AuthDAO authDAO = new AuthDAOmySQL();
        UserDAO userDAO = new UserDAOmySQL();
        GameDAO gameDAO = new GameDAOmySQL();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    /*
     * Receiving messages from clients
     */

    @OnWebSocketMessage
    public void onMessage(Session session, String messageJson) {
        UserGameCommand message = new Gson().fromJson(messageJson, UserGameCommand.class);
        switch (message.getCommandType()) {
            case JOIN_PLAYER -> onJoinPlayerCommand(session, messageJson);
            case JOIN_OBSERVER -> onJoinObserverCommand(session, messageJson);
            case MAKE_MOVE -> onMakeMoveCommand(session, messageJson);
            case RESIGN -> onResignCommand(session, messageJson);
            case LEAVE -> onLeaveCommand(session, messageJson);
        }
    }

    private void onJoinPlayerCommand(Session session, String joinPlayerCommandJson) {
        JoinPlayerCommand command = new Gson().fromJson(joinPlayerCommandJson, JoinPlayerCommand.class);

        // Get the player's username, null if auth token is invalid
        String username = validateAuth(session, command.getAuthString());
        if (username == null) return;

        // Get the game data, null if game does not exist
        GameData gameData = validateGame(session, command.getGameID());
        if (gameData == null) return;

        // Check that the player's username matches the username from the game data
        TeamColor team = command.getPlayerColor();
        String expectedUsername = team.equals(TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
        if (!username.equals(expectedUsername)) {
            sendMessage(session, new ErrorMessage("Error: player position already occupied."));
            return;
        }

        // All successful. Add session to session map and send responses to relevant users.
        webSocketSessions.addSessionToGame(command.getGameID(), command.getAuthString(), session);
        sendMessage(session, new LoadGameMessage(gameData.game()));
        String broadcastMessage = username + " joined the game as " + command.getPlayerColor() + " player";
        broadcastMessage(command.getGameID(), new NotificationMessage(broadcastMessage), session);
    }

    private void onJoinObserverCommand(Session session, String joinObserverCommandJson) {
        JoinObserverCommand command = new Gson().fromJson(joinObserverCommandJson, JoinObserverCommand.class);

        // Get the player's username, null if auth token is invalid
        String username = validateAuth(session, command.getAuthString());
        if (username == null) return;

        // Get the game data, null if game does not exist
        GameData gameData = validateGame(session, command.getGameID());
        if (gameData == null) return;

        // All successful. Add session to session map and send responses to relevant users.
        webSocketSessions.addSessionToGame(command.getGameID(), command.getAuthString(), session);
        sendMessage(session, new LoadGameMessage(gameData.game()));
        String broadcastMessage = username + " joined the game as an observer";
        broadcastMessage(command.getGameID(), new NotificationMessage(broadcastMessage), session);
    }

    private void onMakeMoveCommand(Session session, String makeMoveCommandJson) {
        MakeMoveCommand command = new Gson().fromJson(makeMoveCommandJson, MakeMoveCommand.class);

        // Get the player's username, null if auth token is invalid
        String username = validateAuth(session, command.getAuthString());
        if (username == null) return;

        // Get the game data, null if game does not exist
        GameData gameData = validateGame(session, command.getGameID());
        if (gameData == null) return;

        // Check that there is a piece in the start position of the move
        if (gameData.game().getBoard().getPiece(command.getMove().getStartPosition()) == null) {
            sendMessage(session, new ErrorMessage("Error: no piece in given position"));
            return;
        }

        // Check that the player who is attempting to make the move is in fact the player whose turn it is.
        TeamColor playerColor = getPlayerColor(username, gameData);
        if (playerColor == null) {
            sendMessage(session, new ErrorMessage("Error: game is over"));
            return;
        }
        if (playerColor != gameData.game().getPlayerTurn()) {
            sendMessage(session, new ErrorMessage("Error: not your turn"));
            return;
        }

        // Make the move.
        // Exception occurs if move is invalid or if piece moved does not match the team whose turn it is.
        try {
            gameData.game().makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            sendMessage(session, new ErrorMessage("Error: move not permitted"));
            return;
        }

        // Update the game in the database
        try {
            gameService.updateGame(gameData);
        } catch (Exception e) {
            sendMessage(session, new ErrorMessage("Error: unable to update game in the database"));
            return;
        }

        // Send load game message to all relevant clients
        broadcastMessage(command.getGameID(), new LoadGameMessage(gameData.game()), null);

        // Broadcast a notification to other relevant
        PieceType piece = gameData.game().getBoard().getPiece(command.getMove().getEndPosition()).getPieceType();
        String startPosition = command.getMove().getStartPosition().positionCode();
        String endPosition = command.getMove().getEndPosition().positionCode();
        String broadcastMessage = username + " moved " + piece + " from " + startPosition + " to " + endPosition;
        broadcastMessage(command.getGameID(), new NotificationMessage(broadcastMessage), session);

        // Check for check, checkmate, or stalemate...
        if (validateCheckmate(session, gameData)) return;
        if (validateStalemate(session, gameData)) return;
        validateCheck(gameData);
    }

    private void onResignCommand(Session session, String resignCommandJson) {
        ResignCommand command = new Gson().fromJson(resignCommandJson, ResignCommand.class);

        // Get the player's username, null if auth token is invalid
        String username = validateAuth(session, command.getAuthString());
        if (username == null) return;

        // Get the game data, null if game does not exist
        GameData gameData = validateGame(session, command.getGameID());
        if (gameData == null) return;

        // Check that the player's username matches one of the usernames from the game data
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            sendMessage(session, new ErrorMessage("Error: cannot resign as an observer"));
            return;
        }

        // Check that the game is not already over
        if (gameData.game().getPlayerTurn() == null) {
            sendMessage(session, new ErrorMessage("Error: game is already over"));
            return;
        }

        // Mark the game as over (set the player turn to 'null'). Update it in the database.
        if (markGameOver(session, gameData)) return;

        // Broadcast a message notifying resignation
        String message = " resigned. Game is over.";
        sendMessage(session, new NotificationMessage("You have" + message));
        broadcastMessage(gameData.gameID(), new NotificationMessage(username + " has" + message), session);
    }

    private void onLeaveCommand(Session session, String leaveCommandJson) {
        LeaveCommand command = new Gson().fromJson(leaveCommandJson, LeaveCommand.class);

        // Get the player's username, null if auth token is invalid
        String username = validateAuth(session, command.getAuthString());
        if (username == null) return;

        // Get the game data, null if game does not exist
        GameData gameData = validateGame(session, command.getGameID());
        if (gameData == null) return;

        // Remove user from game in database, if they are a player (not an observer)
        TeamColor team = getPlayerColor(username, gameData);
        if (team != null) {
            if (team.equals(TeamColor.WHITE)) {
                gameData = new GameData(gameData.gameID(), null,
                        gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else {
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                        null, gameData.gameName(), gameData.game());
            }
        }
        try {
            gameService.updateGame(gameData);
        } catch (Exception e) {
            sendMessage(session, new ErrorMessage("Error: unable to update game in database"));
            return;
        }

        // Remove session from game in webSocketSessions
        webSocketSessions.removeSessionFromGame(command.getGameID(), command.getAuthString(), session);

        // Notifiy all other clients that player left
        String broadcastMessage = username + " left the game";
        broadcastMessage(gameData.gameID(), new NotificationMessage(broadcastMessage), session);
    }

    /*
     * Helper methods for message receive handler methods above
     */

    private String validateAuth(Session session, String authToken) {
        // Get the player's username, null if auth token is invalid. Returns username.
        String username = userService.getUsername(authToken);
        if (username == null) sendMessage(session, new ErrorMessage("Error: unauthorized"));
        return username;
    }

    private GameData validateGame(Session session, Integer gameID) {
        // Get the game data, null if game does not exist
        GameData gameData = gameService.getGame(gameID);
        if (gameData == null) sendMessage(session, new ErrorMessage("Error: game does not exist"));
        return gameData;
    }

    private TeamColor getPlayerColor(String username, GameData gameData) {
        if (username.equals(gameData.blackUsername()) || username.equals(gameData.whiteUsername())) {
            return username.equals(gameData.whiteUsername()) ? TeamColor.WHITE : TeamColor.BLACK;
        }
        else return null;
    }

    private boolean markGameOver(Session session, GameData gameData) {
        gameData.game().setPlayerTurn(null);
        try {
            gameService.updateGame(gameData);
        } catch (Exception e) {
            sendMessage(session, new ErrorMessage("Error: unable to update game in the database"));
            return true;
        }
        return false;
    }

    private void validateCheck(GameData gameData) {
        TeamColor playerTurn = gameData.game().getPlayerTurn();
        if (gameData.game().isInCheck(playerTurn)) {
            String message = playerTurn + " player is in check.";
            broadcastMessage(gameData.gameID(), new NotificationMessage(message), null);
        }
    }

    private boolean validateStalemate(Session session, GameData gameData) {
        TeamColor playerTurn = gameData.game().getPlayerTurn();
        if (gameData.game().isInStalemate(playerTurn)) {
            String message = playerTurn + " player has no available moves. Stalemate. Game over.";
            broadcastMessage(gameData.gameID(), new NotificationMessage(message), null);
            markGameOver(session, gameData);
            return true;
        }
        return false;
    }

    private boolean validateCheckmate(Session session, GameData gameData) {
        TeamColor playerTurn = gameData.game().getPlayerTurn();
        TeamColor otherPlayerColor = playerTurn.equals(TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        if (gameData.game().isInCheckmate(playerTurn)) {
            String message = playerTurn + " player is in checkmate. " + otherPlayerColor + " player has won!";
            broadcastMessage(gameData.gameID(), new NotificationMessage(message), null);
            markGameOver(session, gameData);
            return true;
        }
        return false;
    }

    /*
     * Sending messages back to clients
     */

    private void sendMessage(Session session, ServerMessage message) {
        try {
            session.getRemote().sendString(new Gson().toJson(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastMessage(Integer gameID, ServerMessage message, Session exceptThisSession) {
        for (var gameSession : webSocketSessions.getSessionsForGame(gameID).values()) {
            if (!gameSession.equals(exceptThisSession)) {
                sendMessage(gameSession, message);
            }
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {}

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        webSocketSessions.removeSession(session);
    }
}
