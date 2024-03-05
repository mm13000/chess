package dataAccess.auth;

import dataAccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
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
        auths.put(authToken, auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Auth not found");
        } else return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
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
