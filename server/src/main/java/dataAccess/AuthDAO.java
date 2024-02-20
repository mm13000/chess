package dataAccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clearAuths();
}
