package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class AuthDAOTest {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final UserData user1 = new UserData("me","mypass", "me@you.com");
    private final UserData user2 = new UserData("you", "yourpass", "you@me.com");
    AuthDAOTest() {
        this.authDAO = new AuthDAOmySQL();
        this.userDAO = new UserDAOmySQL();
    }

    @BeforeEach
    void setUp() {
        // Clear out all auths and users from database
        try {
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        // Add two users to database
        ArrayList<UserData> newUsers = new ArrayList<>();
        newUsers.add(user1);
        newUsers.add(user2);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (UserData user : newUsers) {
                    stmt.setString(1, user.username());
                    stmt.setString(2, user.password());
                    stmt.setString(3, user.email());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    void goodNewAuth() {
        AuthData auth = null;
        try {
            auth = authDAO.newAuth(user1.username());
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT username, authToken FROM auths WHERE authToken=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, auth.authToken());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Assertions.assertEquals(rs.getString(1), auth.username());
                    Assertions.assertEquals(rs.getString(2), auth.authToken());
                } else Assertions.fail("authToken not found");
            }
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void badNewAuth() {
        // not sure what this entails
    }

    @Test
    void getExistingAuth() {
        try {
            AuthData auth = authDAO.newAuth(user1.username());
            AuthData returnedAuth = authDAO.getAuth(auth.authToken());
            Assertions.assertEquals(auth, returnedAuth);
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void getNonexistentAuth() {
        try {
            authDAO.newAuth(user1.username());
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("fakeAuth"));
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void deleteExistingAuth() {
        try {
            AuthData auth = authDAO.newAuth(user1.username());
            AuthData returnedAuth = authDAO.getAuth(auth.authToken());
            Assertions.assertEquals(auth, returnedAuth);
            authDAO.deleteAuth(auth.authToken());
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void deleteNonexistentAuth() {
        try {
            AuthData auth = authDAO.newAuth(user1.username());
            AuthData returnedAuth = authDAO.getAuth(auth.authToken());
            Assertions.assertEquals(auth, returnedAuth);
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("fakeAuth"));
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void clearAuths() {
        try {
            AuthData auth1 = authDAO.newAuth(user1.username());
            AuthData auth2 = authDAO.newAuth(user2.username());
            authDAO.clearAuths();
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth1.authToken()));
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth2.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }
}