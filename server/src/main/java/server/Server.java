package server;

import com.google.gson.Gson;
import dataAccess.*;
import handler.ErrorMessage;
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
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);

        // Handle any exceptions left unhandled by handlers/services

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearDatabase(Request req, Response res) {
        try {
            gameHandler.clearGames();
            userHandler.clearUsers();
            res.status(200);
            res.body("");
        } catch (DataAccessException ex) {
            res.status(500);
            res.body(new Gson().toJson(new ErrorMessage("Error: " + ex.getMessage())));
        }
        return res.body();
    }

    private Object registerUser(Request req, Response res) {
        userHandler.registerUser(req, res);
        return res.body();
    }

    private Object login(Request req, Response res) {
        userHandler.login(req, res);
        return res.body();
    }

    private Object logout(Request req, Response res) {
        userHandler.logout(req, res);
        return res.body();
    }

    private Object createGame(Request req, Response res) {
        gameHandler.createGame(req, res);
        return res.body();
    }

    private Object joinGame(Request req, Response res) {
        gameHandler.joinGame(req, res);
        return res.body();
    }

    private Object listGames(Request req, Response res) {
        gameHandler.listGames(req, res);
        return res.body();
    }
}
