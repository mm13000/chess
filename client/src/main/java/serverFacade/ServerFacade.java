package serverFacade;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;
import status.StatusCode;

import java.io.*;
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
        String requestBody = new Gson().toJson(request);
        String responseBody = makeRequest(ReqMethod.POST, "/user", null, requestBody);
        return new Gson().fromJson(responseBody, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        String requestBody = new Gson().toJson(request);
        String responseBody = makeRequest(ReqMethod.POST, "/session", null, requestBody);
        return new Gson().fromJson(responseBody, LoginResult.class);
    }

    public void logout(LogoutRequest request) throws ResponseException {
        makeRequest(ReqMethod.DELETE,"/session", request.authToken(), null);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ResponseException {
        String responseBody = makeRequest(ReqMethod.GET, "/game", request.authToken(), null);
        return new Gson().fromJson(responseBody, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        record GameName(String gameName) {}
        String requestBody = new Gson().toJson(new GameName(request.gameName()));
        String responseBody = makeRequest(ReqMethod.POST, "/game", request.authToken(), requestBody);
        return new Gson().fromJson(responseBody, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest request) throws ResponseException {
        record JoinRequest(ChessGame.TeamColor playerColor, int gameID) {}
        String requestBody = new Gson().toJson(new JoinRequest(request.playerColor(), request.gameID()));
        makeRequest(ReqMethod.PUT, "/game", request.authToken(), requestBody);
    }

    /*
     * Helper functions for sending and receiving HTTP requests from the server (all private)
     */

    private enum ReqMethod {DELETE, GET, POST, PUT}

    private String makeRequest(ReqMethod method, String path, String authToken, String body) throws ResponseException {
        if ((method == ReqMethod.PUT || method == ReqMethod.POST) && body == null) {
            throw new RuntimeException("Parameter 'body' is null on PUT or POST method");
        }
        try {
            HttpURLConnection connection = getHttpURLConnection(method, path, authToken, body);
            return readResponse(connection); // return body or throw ResponseException based on status code
        } catch (IOException | URISyntaxException e) {
            throw new ResponseException(StatusCode.ERROR, e.getMessage());
        }
    }

    private static String readResponse(HttpURLConnection connection) throws IOException, ResponseException {
        int responseCode = connection.getResponseCode();
        StringBuilder stringBuilder = new StringBuilder();
        if (responseCode == StatusCode.OK.code) {
            try (InputStream responseBody = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            return stringBuilder.toString();
        } else {
            StatusCode statusCode = switch (responseCode) {
                case 400 -> StatusCode.BAD_REQUEST;
                case 401 -> StatusCode.UNAUTHORIZED;
                case 403 -> StatusCode.TAKEN;
                default -> StatusCode.ERROR;
            };
            try (InputStream responseBody = connection.getErrorStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            throw new ResponseException(statusCode, stringBuilder.toString());
        }
    }

    private HttpURLConnection getHttpURLConnection
            (ReqMethod method, String path, String authToken, String body)
            throws URISyntaxException, IOException {
        URL url = new URI(serverDomain + ":" + serverPort + path).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(20000);
        connection.setRequestMethod(method.toString());

        // Set HTTP request headers, if necessary
        connection.addRequestProperty("Authorization", authToken);
        connection.setDoOutput(true);
        connection.connect();

        // Set HTTP request body, as necessary
        if (method == ReqMethod.PUT || method == ReqMethod.POST) {
            OutputStream requestBodyStream = connection.getOutputStream();
            requestBodyStream.write(body.getBytes());
            requestBodyStream.close();
        }

        return connection;
    }

    public static void main(String[] args) throws ResponseException {
        ServerFacade serverFacade = new ServerFacade("http://localhost", 8080);
        serverFacade.makeRequest(
                ReqMethod.POST, "/user", null,
                "{ \"username\":\"michael3\", \"password\":\"I<3Kaitlyn\", \"email\":\"none\" }");
    }
}
