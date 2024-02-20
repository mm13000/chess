package dataAccess;

import model.UserData;

import java.util.HashSet;

public class UserDAOMemory implements UserDAO {
    private HashSet<UserData> users;

    public UserDAOMemory() {
        users = new HashSet<>();
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() {
        users.clear();
    }
}
