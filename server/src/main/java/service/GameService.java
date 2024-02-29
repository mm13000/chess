package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.GameHeader;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;

public class GameService extends Service {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clearGames() throws DataAccessException {
        gameDAO.clearGames();
    }

    public CreateGameResult createGame(CreateGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException{
        // Check that the request is good
        if (invalidRequest(request)) throw new BadRequestException("One of the fields in the request is null or empty");

        // Check that the user's authToken is valid
        try {
            authDAO.getAuth(request.authToken());
        } catch (DataAccessException ex) {
            throw new UnauthorizedException("Invalid authToken provided. Not authorized.");
        }

        // Then create a game and add it to the database
        int gameID = gameDAO.createGame(request.gameName());

        // Return the result with the new game's gameID
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest request)
            throws BadRequestException, UnauthorizedException, TakenException, DataAccessException {
        // Start by checking that the request is valid
        if (request.authToken() == null || request.authToken().isEmpty() || request.gameID() <=0 ) {
            throw new BadRequestException("One of the required request fields is invalid");
        }
        // Check that the authToken is valid
        AuthData auth;
        try {
            auth = authDAO.getAuth(request.authToken());
        } catch (DataAccessException ex) {
            throw new UnauthorizedException("Invalid authToken provided. Not authorized.");
        }
        // Then try to get the game data
        GameData game;
        try {
            game = gameDAO.getGame(request.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestException("Requested game not found. gameID does not exist in database.");
        }
        // Then check that the requested player color (if applicable) is not yet assigned
        if (request.playerColor() != null) {
            String currentPlayer = switch (request.playerColor()) {
                case WHITE -> game.whiteUsername();
                case BLACK -> game.blackUsername();
            };
            if (currentPlayer != null)
                throw new TakenException("Requested player team already taken by" + currentPlayer);

            // Then update the game with the new player assigned
            GameData newGame;
            newGame = switch (request.playerColor()) {
                case WHITE -> new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
                case BLACK -> new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            };
            gameDAO.updateGame(game.gameID(), newGame);
        }
    }

    public ListGamesResult listGames(ListGamesRequest request) throws UnauthorizedException, DataAccessException {
        // Check that the request is valid
        if (invalidRequest(request)) throw new UnauthorizedException("authToken null or empty");
        // Check that the authToken is valid
        try {
            authDAO.getAuth(request.authToken());
        } catch (DataAccessException ex) {
            throw new UnauthorizedException("Invalid authToken provided. Not authorized.");
        }

        // Then query the database for the list of all games, remove the actual games from it, and return headers
        List<GameData> games = gameDAO.listGames();
        ListGamesResult result = new ListGamesResult(new ArrayList<>());
        for (GameData game : games) {
            result.games().add(new GameHeader(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return result;
    }
}
