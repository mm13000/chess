package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clearGames() throws DataAccessException;
    void createGame(String gameName) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
}
