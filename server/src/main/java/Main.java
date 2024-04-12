import dataAccess.DataAccessException;
import dataAccess.auth.AuthDAO;
import dataAccess.auth.AuthDAOmySQL;
import dataAccess.game.GameDAO;
import dataAccess.game.GameDAOmySQL;
import dataAccess.user.UserDAO;
import dataAccess.user.UserDAOmySQL;

public class Main {
    public static void main(String[] args) {
        // Clear database
        AuthDAO authDAO = new AuthDAOmySQL();
        UserDAO userDAO = new UserDAOmySQL();
        GameDAO gameDAO = new GameDAOmySQL();
        try {
            gameDAO.clearGames();
            authDAO.clearAuths();
            userDAO.clearUsers();
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }
    }
}