package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOMemory;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOMemory;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOMemory;
import dataAccess.user.UserDAOmySQL;
import exception.ResponseException;
import handler.GameHandler;
import handler.UserHandler;
import spark.*;

import java.sql.SQLException;

public class Server {
    private final GameHandler gameHandler;
    private final UserHandler userHandler;
    private enum DatabaseType {
        MEMORY,
        MYSQL
    }

    public Server() {
        // Create DAOs that will be shared by everyone
        AuthDAO authDAO = null;
        GameDAO gameDAO = null;
        UserDAO userDAO = null;

        // Initialize DAOs and setup database as needed:
        DatabaseType dbtype = DatabaseType.MYSQL; // CHANGE THIS TO CHANGE DATABASE TYPE
        switch (dbtype) {
            case MEMORY:
                authDAO = new AuthDAOMemory();
                gameDAO = new GameDAOMemory();
                userDAO = new UserDAOMemory();
                break;
            case MYSQL:
                authDAO = new AuthDAOmySQL();
                gameDAO = new GameDAOmySQL();
                userDAO = new UserDAOmySQL();
                setupDatabase();
                break;
        }

        // Create handler objects using the DAOs
        gameHandler = new GameHandler(gameDAO, authDAO);
        userHandler = new UserHandler(userDAO, authDAO);
    }

    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080);
        System.out.println("Started HTTP server on " + port);
    }

    public int run(int desiredPort) {
        // Setup
        Spark.port(desiredPort);
        Spark.staticFiles.location("web"); // Website setup

        // Register websocket upgrade endpoint
        Spark.webSocket("/connect", WebSocketServer.class);

        // Register endpoints
        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);

        // Handle any exceptions left unhandled by handlers/services
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object exceptionHandler(ResponseException e, Request req, Response res) {
        res.status(e.statusCode().code);
        String message = switch (e.statusCode()) {
            case BAD_REQUEST -> "Error: bad request";
            case UNAUTHORIZED -> "Error: unauthorized";
            case TAKEN -> "Error: already taken";
            default -> "Error: " + e.getMessage();
        };
        record ErrorMessage(String message) {}
        res.body(new Gson().toJson(new ErrorMessage(message)));
        return res.body();
    }

    private Object clearDatabase(Request req, Response res) throws ResponseException {
        gameHandler.clearGames();
        userHandler.clearUsers();
        res.status(200);
        res.body("");
        return res.body();
    }

    private Object registerUser(Request req, Response res) throws ResponseException {
        userHandler.registerUser(req, res);
        return res.body();
    }

    private Object login(Request req, Response res) throws ResponseException {
        userHandler.login(req, res);
        return res.body();
    }

    private Object logout(Request req, Response res) throws ResponseException {
        userHandler.logout(req, res);
        return res.body();
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        gameHandler.createGame(req, res);
        return res.body();
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        gameHandler.joinGame(req, res);
        return res.body();
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        gameHandler.listGames(req, res);
        return res.body();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) UNIQUE NOT NULL,
                game_data TEXT NOT NULL,
                FOREIGN KEY(whiteUsername) REFERENCES users(username),
                FOREIGN KEY(blackUsername) REFERENCES users(username)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
                username VARCHAR(255) NOT NULL,
                authToken VARCHAR(255) NOT NULL UNIQUE KEY,
                FOREIGN KEY(username) REFERENCES users(username)
            );
            """
    };

    private void setupDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Database not successfully created. Exception thrown: " + ex.getMessage());
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException ex) {
            throw new RuntimeException("Unable to setup databse. Exception thrown: " + ex.getMessage());
        }
    }
}
