package serverFacade;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class WebSocketServerFacade extends Endpoint {
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket Session opened.");
    }

//    @Override
    public void onMessage() {

    }
}
