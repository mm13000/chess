package dataAccess.game;

import dataAccess.DataAccessException;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void clearGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
}
