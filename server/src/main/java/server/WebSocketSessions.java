package server;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;

public class WebSocketSessions {
    private final HashMap<Integer, HashMap<String, Session>> sessionMap = new HashMap<>();

    public void addSessionToGame(Integer gameID, String authToken, Session session) {
        // Add the session to the map (creates a new session map for the game if needed)
        var gameSessions = sessionMap.computeIfAbsent(gameID, k -> new HashMap<>());
        gameSessions.put(authToken, session);
    }

    public void removeSessionFromGame(Integer gameID, String authToken, Session session) {
        var gameSessions = sessionMap.get(gameID);
        if (gameSessions != null) {
            gameSessions.remove(authToken);
        }
    }

    public void removeSession(Session session) {
        for (var gameID : sessionMap.keySet()) {
            for (var authToken : sessionMap.get(gameID).keySet()) {
                if (sessionMap.get(gameID).get(authToken).equals(session)) {
                    sessionMap.get(gameID).remove(authToken);
                }
            }
        }
    }

    public Map<String, Session> getSessionsForGame(Integer gameID) {
        return sessionMap.get(gameID);
    }
}
