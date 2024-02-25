package serviceTests;

import dataAccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import request.RegisterRequest;
import result.RegisterResult;
import service.BadRequestException;
import service.NameTakenException;
import service.UserService;

class UserServiceTests {
    UserDAO testUserDAO;
    AuthDAO testAuthDAO;
    UserService testUserService;

    @BeforeEach
    public void setup() {
        testUserDAO = new UserDAOMemory();
        testAuthDAO = new AuthDAOMemory();
        testUserService = new UserService(testUserDAO, testAuthDAO);
    }

    @Test
    void clearUsersAndAuths() {

    }

    @Test
    void registerUserNewUser() throws TestException {
        RegisterRequest goodRequest = new RegisterRequest("bob1", "1234", "bob1@gmail.com");
        Assertions.assertDoesNotThrow(() -> testUserService.registerUser(goodRequest),
                "Unexpected exception was thrown during regular user registration");
        Assertions.assertThrows(NameTakenException.class, () -> testUserService.registerUser(goodRequest),
                "registerUser() did not throw NameTaken exception");
    }

    @Test
    void registerUserCorrectResult() {
        RegisterRequest goodRequest = new RegisterRequest("bob1", "1234", "bob1@gmail.com");
        RegisterResult actualResult;
        try {
            actualResult = testUserService.registerUser(goodRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RegisterResult expectedResult = new RegisterResult("bob1", "");
        Assertions.assertEquals(expectedResult.username(), actualResult.username(),
                "registerUser() did not return the correct username result");
    }

    @Test
    void registerUserBadRequest() {
        RegisterRequest badRequest = new RegisterRequest("bob1", null, "bob1@gmail.com");
        Assertions.assertThrows(BadRequestException.class, () -> testUserService.registerUser(badRequest),
                "registerUser() did not throw a BadRequestException when given null password");
    }
}