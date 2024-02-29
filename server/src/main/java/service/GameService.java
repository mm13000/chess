package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;

public class GameService extends Service {
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
