package handler;

import service.GameService;

public class GameHandler {
    private final GameService gameService;

    public GameHandler() {
        this.gameService = new GameService();
    }

    public void clearGames() {
        gameService.clearGames();
    }
}
