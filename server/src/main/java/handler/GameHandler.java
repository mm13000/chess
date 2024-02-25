package handler;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import service.GameService;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public void clearGames() {
        gameService.clearGames();
    }
}
