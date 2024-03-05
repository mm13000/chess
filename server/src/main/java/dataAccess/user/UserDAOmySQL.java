package dataAccess.user;

import dataAccess.DataAccessException;
import model.UserData;

public class UserDAOmySQL implements UserDAO {
    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }
}
