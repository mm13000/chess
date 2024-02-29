package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import service.BadRequestException;
import service.GameService;
import service.TakenException;
import service.UnauthorizedException;
import spark.Request;
import spark.Response;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public void clearGames() throws DataAccessException {
        gameService.clearGames();
    }

    public void createGame(Request httpReq, Response response) {
        GameNameNoAuth gameName = new Gson().fromJson(httpReq.body(), GameNameNoAuth.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(httpReq.headers("Authorization"), gameName.gameName());
        CreateGameResult result;
        try {
            result = gameService.createGame(createGameRequest);
        } catch (BadRequestException e) {
            response.status(400);
            response.body(new Gson().toJson(new ErrorMessage("Error: bad request")));
            return;
        } catch (UnauthorizedException e) {
            response.status(401);
            response.body(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
            return;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return;
        }
        response.status(200);
        response.body(new Gson().toJson(result));
    }

    public void joinGame(Request httpReq, Response response) {
        JoinGameNoAuth joinGameNoAuth = new Gson().fromJson(httpReq.body(), JoinGameNoAuth.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(httpReq.headers("Authorization"),
                joinGameNoAuth.playerColor(), joinGameNoAuth.gameID());
        try {
            gameService.joinGame(joinGameRequest);
        } catch (BadRequestException e) {
            response.status(400);
            response.body(new Gson().toJson(new ErrorMessage("Error: bad request")));
            return;
        } catch (UnauthorizedException e) {
            response.status(401);
            response.body(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
            return;
        } catch (TakenException e) {
            response.status(403);
            response.body(new Gson().toJson(new ErrorMessage("Error: already taken")));
            return;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return;
        }
        response.status(200);
        response.body("");
    }

    public void listGames(Request httpReq, Response response) {
        ListGamesRequest listGamesRequest = new ListGamesRequest(httpReq.headers("Authorization"));
        ListGamesResult result;
        try {
            result = gameService.listGames(listGamesRequest);
        } catch (UnauthorizedException e) {
            response.status(401);
            response.body(new Gson().toJson(new ErrorMessage("Error: unauthorized")));
            return;
        } catch (Exception e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return;
        }
        response.status(200);
        response.body(new Gson().toJson(result));
    }
}
