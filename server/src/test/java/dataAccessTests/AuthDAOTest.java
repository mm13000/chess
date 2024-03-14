package dataAccessTests;

import dataAccess.*;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.UUID;

class AuthDAOTest {
    private final AuthDAO authDAO = new AuthDAOmySQL();
    private final UserDAO userDAO = new UserDAOmySQL();
    private final UserData user1 = new UserData("me","mypass", "me@you.com");
    private final UserData user2 = new UserData("you", "yourpass", "you@me.com");
    private final UUIDGenerator mockUUIDGenerator = new UUIDGenerator() {
        // Override the generateUUID method to return a known UUID
        @Override
        public UUID generateUUID() {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
    };

    @BeforeEach
    void setUp() {
        try {
            authDAO.clearAuths();
            userDAO.clearUsers();
            insertUsers(user1, user2);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    private void insertUsers(UserData... users) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
            for (UserData user : users) {
                stmt.setString(1, user.username());
                stmt.setString(2, user.password());
                stmt.setString(3, user.email());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void goodNewAuth() {
        try {
            AuthData auth = authDAO.newAuth(user1.username());
            Assertions.assertEquals(auth, authDAO.getAuth(auth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void badNewAuth() {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.newAuth("nonexistentUser"));
    }

    @Test
    void testNewDuplicateAuthToken() {
        AuthDAO fakeAuthDAO = new AuthDAOmySQL(mockUUIDGenerator);
        Assertions.assertDoesNotThrow(() -> fakeAuthDAO.newAuth(user1.username()));
        Assertions.assertThrows(DataAccessException.class, () -> fakeAuthDAO.newAuth(user1.username()));
    }

    @Test
    void getExistingAuth() {
        assertAuth(user1);
    }

    @Test
    void getNonexistentAuth() {
        assertNonexistentAuth("fakeAuth");
    }

    @Test
    void deleteExistingAuth() {
        assertDeleteAuth(user1);
    }

    @Test
    void deleteNonexistentAuth() {
        assertNonexistentAuth("fakeAuth");
    }

    @Test
    void clearAuths() {
        try {
            authDAO.newAuth(user1.username());
            authDAO.newAuth(user2.username());
            authDAO.clearAuths();
            assertNonexistentAuth(user1.username());
            assertNonexistentAuth(user2.username());
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    private void assertAuth(UserData user) {
        try {
            AuthData auth = authDAO.newAuth(user.username());
            Assertions.assertEquals(auth, authDAO.getAuth(auth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    private void assertNonexistentAuth(String authToken) {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken));
    }

    private void assertDeleteAuth(UserData user) {
        try {
            AuthData auth = authDAO.newAuth(user.username());
            authDAO.deleteAuth(auth.authToken());
            assertNonexistentAuth(auth.authToken());
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }
}