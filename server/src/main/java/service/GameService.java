package service;

import dataAccess.GameDAO;
import dataAccess.GameDAOMemory;

public class GameService {
    private final GameDAO gameDAO;

    public GameService() {
        this.gameDAO = new GameDAOMemory();
    }

    public void clearGames() {
        gameDAO.clearGames();
    }
}
