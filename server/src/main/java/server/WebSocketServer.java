package server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WebSocketServer {

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("WebSocket Server received message: " + message);
    }
}
