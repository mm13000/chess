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
import handler.ErrorMessage;
import handler.GameHandler;
import handler.UserHandler;
import spark.*;

import java.sql.SQLException;

public class Server {
    private final GameHandler gameHandler;
    private final UserHandler userHandler;
    private enum databaseType {
        MEMORY,
        MYSQL
    }

    public Server() {
        // Create DAOs that will be shared by everyone
        AuthDAO authDAO;
        GameDAO gameDAO;
        UserDAO userDAO;

        // Initialize DAOs and setup database as needed:
        databaseType dbtype = databaseType.MYSQL; // CHANGE THIS TO CHANGE DATABASE TYPE
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
            case null, default:
                authDAO = new AuthDAOMemory();
                gameDAO = new GameDAOMemory();
                userDAO = new UserDAOMemory();
        }

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
