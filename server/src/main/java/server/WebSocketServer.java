package server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WebSocketServer {

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("WebSocket Server received message: " + message);
        session.getRemote().sendString("Thank you for sending: " + message);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {

    }
}
