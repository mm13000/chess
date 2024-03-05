package dataAccess.auth;

import dataAccess.DataAccessException;
import model.AuthData;

public class AuthDAOmySQL implements AuthDAO {
    @Override
    public AuthData newAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAuths() throws DataAccessException {

    }
}
