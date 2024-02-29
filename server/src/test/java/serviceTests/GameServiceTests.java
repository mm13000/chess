package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;

public class GameServiceTests {
    ServiceTestFactory testFactory;
    GameService gameService;
    GameDAO gameDAO;
    AuthDAO authDAO;

    @BeforeEach
    public void Setup() {
        testFactory = new ServiceTestFactory();
        gameDAO = testFactory.gameDAO;
        authDAO = testFactory.authDAO;
        gameService = testFactory.gameService;
    }

    /*
     * Tests of GameService.createGame()
     */
    @Test
    public void createValidGame() {

//        CreateGameRequest request = new CreateGameRequest();
    }

}
