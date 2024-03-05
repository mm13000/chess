package dataAccess.game;

import dataAccess.DataAccessException;
import model.GameData;

import java.util.List;

public class GameDAOmySQL implements GameDAO {
    @Override
    public void clearGames() throws DataAccessException {

    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {

    }
}
