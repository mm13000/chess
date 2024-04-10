package serverFacade;

import javax.websocket.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WebSocketClient extends Endpoint implements MessageHandler.Whole<String> {
    private final Session session;
    public WebSocketClient() throws Exception {
        // Connect to the server and get a WebSocket session
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        // Register the message handler class (this class, since it implements MessageHandler.Whole<>)
        this.session.addMessageHandler(this);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Server sent message: " + message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void send(String message) throws Exception {
        this.session.getBasicRemote().sendText(message);
    }

    public static void main(String[] args) throws Exception {
        var wsclient = new WebSocketClient();
        System.out.print("Enter a message to send: ");
        while (true) wsclient.send(new Scanner(System.in).nextLine());
    }
}
