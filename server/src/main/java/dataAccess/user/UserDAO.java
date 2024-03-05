package dataAccess.user;

import dataAccess.DataAccessException;
import model.UserData;

public interface UserDAO {
    void addUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
