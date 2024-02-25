package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.GameDAOMemory;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clearGames() {
        gameDAO.clearGames();
    }
}
