package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;

public class GameDAOMemory implements GameDAO {
    private HashSet<GameData> games;

    public GameDAOMemory() {
        games = new HashSet<>();
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
