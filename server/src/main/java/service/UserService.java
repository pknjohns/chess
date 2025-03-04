package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    private final AuthDAO authDB;
    private final UserDAO userDB;

    public UserService(AuthDAO authDB, UserDAO userDB) {
        this.authDB = authDB;
        this.userDB = userDB;
    }

    public UserData findUsername(UserData user) throws DataAccessException {
        return userDB.getUser(user);
    }





}
