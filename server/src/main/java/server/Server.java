package server;

import dataAccess.*;
import handler.GameHandler;
import handler.UserHandler;
import spark.*;

public class Server {
    private final GameHandler gameHandler;
    private final UserHandler userHandler;

    public Server() {
        // Create the DAOs that will be shared by everyone
        AuthDAO authDAO = new AuthDAOMemory();
        GameDAO gameDAO = new GameDAOMemory();
        UserDAO userDAO = new UserDAOMemory();

        // Create handler objects using the DAOs
        gameHandler = new GameHandler(gameDAO, authDAO);
        userHandler = new UserHandler(userDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register endpoints
        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);

        // Handle any exceptions left unhandled by handlers/services

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearDatabase(Request req, Response res) {
        gameHandler.clearGames();
        userHandler.clearUsers();
        res.status(200);
        return "";
    }

    private Object registerUser(Request req, Response res) {
        return "";
    }
}
