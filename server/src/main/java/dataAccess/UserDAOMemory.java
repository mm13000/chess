package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;

public class UserDAOMemory implements UserDAO {
    private final HashMap<String, UserData> users;

    public UserDAOMemory() {
        users = new HashMap<>();
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User not found");
        } else return users.get(username);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        for (var entry : users.entrySet()) {
            var user = entry.getValue();
            returnString.append(user.toString());
            returnString.append("\n");
        }
        return returnString.toString();
    }
}
