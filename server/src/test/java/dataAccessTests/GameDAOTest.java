package dataAccessTests;

import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class GameDAOTest {
    private final GameDAO gameDAO = new GameDAOmySQL();
    private final AuthDAO authDAO = new AuthDAOmySQL();
    private final UserDAO userDAO = new UserDAOmySQL();

    @BeforeEach
    void setUp() {
    }

    @Test
    void clearGames() {
    }

    @Test
    void createGameSuccess() {
    }

    @Test
    void listGames() {
    }

    @Test
    void getGame() {
    }

    @Test
    void updateGame() {
    }
}