package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class AuthDAOMemory implements AuthDAO {
    private HashSet<AuthData> auths;

    public AuthDAOMemory() {
        auths = new HashSet<>();
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clearAuths() {
        auths.clear();
    }
}
