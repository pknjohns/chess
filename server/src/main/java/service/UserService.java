package service;

import dataaccess.*;

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

    public RegisterResult registerUser(RegisterRequest request) throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = request.username();
        String password = request.password();
        String email = request.email();

        if (username == null) {
            throw new BadRequestException("Missing username");
        } else if (password == null) {
            throw new BadRequestException("Missing password");
        } else if (email == null) {
            throw new BadRequestException("Missing email");
        }

        if (userDB.getUser(username) == null) { //username is available
            UserData user = new UserData(username, password, email);
            String token = generateToken();
            AuthData auth = new AuthData(token, username);
            userDB.addUser(user);
            authDB.addAuth(auth);
            return new RegisterResult(token, username);
        } else {
            throw new AlreadyTakenException("Username is already in use");
        }
    }

    public UserData findUsername(String username) throws DataAccessException {
        return userDB.getUser(username);
    }

    public void createUser(UserData user) throws DataAccessException {
        userDB.addUser(user);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(generateToken(), username);
        authDB.addAuth(auth);
    }

}
