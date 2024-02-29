package serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.BadRequestException;
import service.NameTakenException;
import service.UnauthorizedException;
import service.UserService;

import java.util.UUID;

class UserServiceTests {
    ServiceTestFactory testFactory;
    UserDAO userDAO;
    AuthDAO authDAO;
    UserService userService;

    @BeforeEach
    public void setup() {
        testFactory = new ServiceTestFactory();
        userDAO = testFactory.userDAO;
        authDAO = testFactory.authDAO;
        userService = testFactory.userService;
    }


    /*
     * Tests of clearUsersAndAuths()
     */

    @Test
    void clearUsersAndAuths() {

    }

    /*
     * Tests of UserService.registerUser()
     */

    @Test
    void registerUserNewUser() {
        RegisterRequest goodRequest = new RegisterRequest("bob1", "1234", "bob1@gmail.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(goodRequest),
                "Unexpected exception was thrown during regular user registration");
        Assertions.assertThrows(NameTakenException.class, () -> userService.registerUser(goodRequest),
                "registerUser() did not throw NameTaken exception");
    }

    @Test
    void registerUserCorrectResult() {
        RegisterRequest goodRequest = new RegisterRequest("bob1", "1234", "bob1@gmail.com");
        RegisterResult actualResult;
        try {
            actualResult = userService.registerUser(goodRequest);
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
        Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(badRequest),
                "registerUser() did not throw a BadRequestException when given null password");
    }

    /*
     * Tests of UserService.login()
     */

    @Test
    void loginExistingUser() {
        // First register a user so that the user exists in the server
        var user = testFactory.registerUser(new UserData("bob","pass","bob@me.com"));
        // Then send a valid login request
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        try {
            LoginResult result = userService.login(loginRequest);
            // Test that the LoginResult we receive has correct username and a valid authToken
            Assertions.assertEquals(user.username(), result.username(), "Incorrect username returned");
            Assertions.assertDoesNotThrow(() -> UUID.fromString(result.authToken()), "Invalid UUID returned");
        } catch (Exception e) {
            Assertions.fail("Test failed because an exception was thrown: " + e.getMessage());
        }
    }

    @Test
    void LoginWrongPassword() {
        // First register a user so that a user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then send a login request with the wrong password
        LoginRequest loginRequest = new LoginRequest(user.username(), "wrongPassword");
        // an "UnauthorizedException" should be thrown
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    @Test
    void LoginNonexistentUser() {
        // First register a user so that a user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then send a login request for a nonexistent user
        LoginRequest loginRequest = new LoginRequest("mallory", user.password());
        // an "UnauthorizedException" should be thrown
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    @Test
    void LoginBadRequest() {
        // Bad request because of null username
        LoginRequest nullUsername = new LoginRequest(null, "1234");
        Assertions.assertThrows(BadRequestException.class, () -> userService.login(nullUsername));
        // Bad request because of null password
        LoginRequest nullPassword = new LoginRequest("bob", null);
        Assertions.assertThrows(BadRequestException.class, () -> userService.login(nullPassword));
        // Bad request because of empty username
        LoginRequest emptyUsername = new LoginRequest("", "1234");
        Assertions.assertThrows(BadRequestException.class, () -> userService.login(emptyUsername));
        // Bad request because of empty password
        LoginRequest emptyPassword = new LoginRequest("bob", "");
        Assertions.assertThrows(BadRequestException.class, () -> userService.login(emptyPassword));
    }

    /*
     * Tests of UserService.logout()
     */

    @Test
    void logoutExistingUser() {
        // First register a user so that the user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then login with that user to get an authToken
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        LoginResult loginResult;
        try {
            loginResult = userService.login(loginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Then send a logout request with that authToken
        try {
            userService.logout(new LogoutRequest(loginResult.authToken()));
        } catch (Exception e) {
            Assertions.fail("Test failed because an exception was thrown: " + e.getMessage());
        }
        // Then ensure that the authToken is no longer valid
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    void logoutNonexistentAuth() {
        // Try logging someone out when there is no one yet stored in the database
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout(new LogoutRequest("1122")));
        // First register a user so that the user exists in the server
        var user = testFactory.registerUser(new UserData("bob","1234","bob@me.com"));
        // Then login with that user to get an authToken
        LoginRequest loginRequest = new LoginRequest(user.username(), user.password());
        try {
            userService.login(loginRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Then send a logout request with a nonexistent authToken
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout(new LogoutRequest("nonexistentAuthToken")));
    }

    @Test
    void logoutBadRequest() {
        // Bad request because of null authToken
        LogoutRequest nullAuthToken = new LogoutRequest(null);
        Assertions.assertThrows(BadRequestException.class, () -> userService.logout(nullAuthToken));
        // Bad request because of empty authToken
        LogoutRequest emptyAuthToken = new LogoutRequest("");
        Assertions.assertThrows(BadRequestException.class, () -> userService.logout(emptyAuthToken));
    }
}