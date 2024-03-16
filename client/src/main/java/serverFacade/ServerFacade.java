package serverFacade;

import exception.ResponseException;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

import java.io.IOException;
import java.net.*;

public class ServerFacade {
    /*
     * The ServerFacade class is the only one (in the client) that knows anything about the
     * actual functionality of the server.
     * Other classes will have access to a ServerFacade object. They will call methods on it to use the server.
     * Only the ServerFacade uses HTTP requests or other server-specific things.
     * It will use request and result classes. It receives a request object, uses that to create an HTTP request and
     * call the server, receives back a
     */

    private final String serverDomain;
    private final int serverPort;

    public ServerFacade(String domain, int port) {
        // Save domain and port for future connections to the server (server must already be running)
        serverDomain = domain;
        serverPort = port;
    }

    /*
     * Public methods called to interface with the server
     */

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        return null;
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        return null;
    }

    public void logout(LogoutRequest request) throws ResponseException {

    }

    public ListGamesResult listGames(ListGamesRequest request) {
        return null;
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        return null;
    }

    public void joinGame(JoinGameRequest request) {

    }

    /*
     * Helper functions for sending and receiving HTTP requests from the server (all private)
     */

    private void makeHttpRequest(String method) {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URI(serverDomain + ":" + serverPort).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RuntimeException("Method parameter " + method + " invalid. Exception: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Call to construct a URI from urlString threw exception: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("IllegalArgumentException thrown. URL may not be absolute. " + e.getMessage());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Server domain URL invalid. Exception thrown: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O exception occurred with url.openConnection() method: " + e.getMessage());
        }

    }
}
