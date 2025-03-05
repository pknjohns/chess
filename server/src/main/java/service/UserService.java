package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import java.util.UUID;

public class UserService {

    private final AuthDAO authDB;
    private final UserDAO userDB;

    public UserService(AuthDAO authDB, UserDAO userDB) {
        this.authDB = authDB;
        this.userDB = userDB;
    }

//    public RegisterResult registerUser(RegisterRequest request) throws DataAccessException{
//
//        return null;
//    }

    public UserData findUsername(UserData user) throws DataAccessException {
        return userDB.getUser(user);
    }

    public void createUser(UserData user) throws DataAccessException {
        userDB.addUser(user);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(generateToken(), username);
        authDB.addToken(auth);
    }

}
