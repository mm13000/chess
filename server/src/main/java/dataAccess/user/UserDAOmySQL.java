package dataAccess.user;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOmySQL implements UserDAO {
    @Override
    public void addUser(UserData user) throws DataAccessException {
        // SQL command for adding a user
        String sql = "INSERT INTO users (username, password, email) values (?, ?, ?)";

        // Connect to the database, prepare the statement, and execute it to insert the user
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.email());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        // SQL command for retrieving the row for a given user
        String sql = "SELECT username, password, email FROM users WHERE username=?";

        // Connect to the database, prepare the statement, and execute it to insert the user
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // execute the query and save the result
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                // parse throuth the result and return the user data
                UserData user;
                if (rs.next()) {
                    user = new UserData(rs.getString(1),
                            rs.getString(2), rs.getString(3));
                } else {
                    throw new DataAccessException("username not found in database");
                }
                return user;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }

    public static void main(String[] args) {
        UserData user = new UserData("kaitlyn", "mypassword", "michael@me.com");
        UserDAO userDAO = new UserDAOmySQL();
        try {
            System.out.println(userDAO.getUser(user.username()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}