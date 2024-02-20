package server;

import handler.AuthHandler;
import handler.GameHandler;
import handler.UserHandler;
import spark.*;

public class Server {
    private final AuthHandler authHandler;
    private final GameHandler gameHandler;
    private final UserHandler userHandler;

    public Server() {
        authHandler = new AuthHandler();
        gameHandler = new GameHandler();
        userHandler = new UserHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register endpoints
        Spark.delete("/db", this::clearDatabase);

        // Handle any exceptions left unhandled by handlers/services

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearDatabase(Request req, Response res) {
        authHandler.clearAuths();
        gameHandler.clearGames();
        userHandler.clearUsers();
        res.status(200);
        return "";
    }
}
