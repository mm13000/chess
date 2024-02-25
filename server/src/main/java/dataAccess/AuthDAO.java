package dataAccess;

import model.AuthData;
import model.UserData;


public interface AuthDAO {
    AuthData newAuth(String username) throws DataAccessException;
    AuthData getAuth(UserData userData) throws DataAccessException;
    void deleteAuth(UserData userData);
    void clearAuths();
}
