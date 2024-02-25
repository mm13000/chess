package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class AuthDAOMemory implements AuthDAO {
    private final HashMap<String, AuthData> auths;

    public AuthDAOMemory() {
        auths = new HashMap<>();
    }

    @Override
    public AuthData newAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(username, authToken);
        auths.put(username, auth);
        return auth;
    }

    @Override
    public AuthData getAuth(UserData userData) throws DataAccessException {
        if (!auths.containsKey(userData.username())) {
            throw new DataAccessException("Auth not found");
        } else return auths.get(userData.username());
    }

    @Override
    public void deleteAuth(UserData userData) {
        auths.remove(userData.username());
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        for (var entry : auths.entrySet()) {
            AuthData auth = entry.getValue();
            returnString.append(auth.toString());
            returnString.append("\n");
        }
        return returnString.toString();
    }
}
