package dataAccess.game;

import chess.ChessGame;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameDAOMemory implements GameDAO {
    private final HashMap<Integer, GameData> games;
    private int nextID;

    public GameDAOMemory() {
        games = new HashMap<>();
        nextID = 1;
    }

    @Override
    public void clearGames() {
        games.clear();
    }

    @Override
    public int createGame(String gameName) {
        int id = nextID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);
        return id;
    }

    @Override
    public List<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found. Invalid gameID.");
        }
        return games.get(gameID);
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found. Invalid gameID.");
        }
        games.put(gameID, updatedGame);
    }
}
