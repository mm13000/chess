package handler;

import com.google.gson.Gson;
import dataAccess.auth.AuthDAO;
import dataAccess.game.GameDAO;
import exception.ResponseException;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public void clearGames() throws ResponseException {
        gameService.clearGames();
    }

    public void createGame(Request httpReq, Response response) throws ResponseException {
        GameNameNoAuth gameName = new Gson().fromJson(httpReq.body(), GameNameNoAuth.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(httpReq.headers("Authorization"), gameName.gameName());
        CreateGameResult result;
        result = gameService.createGame(createGameRequest);

        response.status(200);
        response.body(new Gson().toJson(result));
    }

    public void joinGame(Request httpReq, Response response) throws ResponseException {
        JoinGameNoAuth joinGameNoAuth = new Gson().fromJson(httpReq.body(), JoinGameNoAuth.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(httpReq.headers("Authorization"),
                joinGameNoAuth.playerColor(), joinGameNoAuth.gameID());

        gameService.joinGame(joinGameRequest);
        response.status(200);
        response.body("");
    }

    public void listGames(Request httpReq, Response response) throws ResponseException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(httpReq.headers("Authorization"));
        ListGamesResult result;

        result = gameService.listGames(listGamesRequest);
        response.status(200);
        response.body(new Gson().toJson(result));
    }
}
