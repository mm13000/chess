package dataAccess;

import model.AuthData;

public class AuthDAOMemory implements AuthDAO {
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
}
