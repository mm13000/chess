package dataAccess;

import model.AuthData;


public interface AuthDAO {
    AuthData newAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clearAuths() throws DataAccessException;
}
