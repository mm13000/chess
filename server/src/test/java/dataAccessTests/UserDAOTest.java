package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private final UserDAO userDAO = new UserDAOmySQL();
    private final AuthDAO authDAO = new AuthDAOmySQL();
    private final UserData user1 = new UserData("me","mypass", "me@you.com");
    private final UserData user2 = new UserData("you", "yourpass", "you@me.com");

    @BeforeEach
    void setUp() {
        // Clear the database
        try {
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void addUserSuccess() {
        try {
            userDAO.addUser(user1);
            userDAO.addUser(user2);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void addUserNullFields() {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.addUser(new UserData(null, "pass", "email")));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.addUser(new UserData("user", null, "email")));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.addUser(new UserData("user", "pass", null)));
    }

    @Test
    void addUserDuplicateUsername() {
        try {
            userDAO.addUser(user1);
            Assertions.assertThrows(DataAccessException.class, () -> userDAO.addUser(user1));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void getUser() {
        try {
            userDAO.addUser(user1);
            userDAO.addUser(user2);
            Assertions.assertEquals(user1, userDAO.getUser(user1.username()));
            Assertions.assertEquals(user2, userDAO.getUser(user2.username()));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void getUserNonexistentUsername() {
        try {
            userDAO.addUser(user1);
            Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("nonexistentUser"));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void getUserNullUsername() {
        // Try when no users have been added
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(null));

        // Add a user and try again
        try {
            userDAO.addUser(user1);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(null));
    }

    @Test
    void clearUsers() {
        try {
            userDAO.clearUsers();
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
            assertFalse(stmt.executeQuery().next());
        } catch (Exception e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }
}